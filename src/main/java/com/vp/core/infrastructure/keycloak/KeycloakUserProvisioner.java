package com.vp.core.infrastructure.keycloak;

import com.vp.core.domain.exceptions.DomainException;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.user.ScopeType;
import com.vp.core.domain.user.User;
import com.vp.core.domain.validation.DomainError;
import com.vp.core.infrastructure.config.KeycloakAdminProperties;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakUserProvisioner {

    private static final Logger log = LoggerFactory.getLogger(KeycloakUserProvisioner.class);

    private final KeycloakAdminProperties props;
    private final MerchantGateway merchantGateway;

    public KeycloakUserProvisioner(
            final KeycloakAdminProperties props,
            final MerchantGateway merchantGateway
    ) {
        this.props = props;
        this.merchantGateway = merchantGateway;
    }

    /**
     * Cria utilizador convidado no Keycloak (credenciais, atributos e client role).
     *
     * @return id interno do utilizador no Keycloak (para compensação)
     */
    public String provisionInvitedUser(final User user) {
        validateAdminConfig();

        final var scope = user.getScope();
        if (scope.getType() == ScopeType.PLATFORM) {
            throw DomainException.with(new DomainError("Provisionamento Keycloak não suportado para escopo PLATFORM."));
        }

        final String tenantIdAttr;
        final String merchantIdAttr;
        if (scope.isTenant()) {
            tenantIdAttr = scope.getScopeId();
            merchantIdAttr = null;
        } else {
            final var merchantId = MerchantId.from(scope.getScopeId());
            final var merchant = merchantGateway.findById(merchantId)
                    .orElseThrow(() -> NotFoundException.with(Merchant.class, merchantId));
            tenantIdAttr = merchant.tenantId().getValue();
            merchantIdAttr = scope.getScopeId();
        }

        final String roleName = scope.isTenant() ? "tenant_admin" : "merchant_admin";

        try (Keycloak keycloak = buildKeycloak()) {
            final RealmResource realm = keycloak.realm(props.realm());

            final var representation = new UserRepresentation();
            representation.setUsername(user.getEmail().getValue());
            representation.setEmail(user.getEmail().getValue());
            representation.setEnabled(true);
            representation.setEmailVerified(true);
            applyNameParts(user.getName(), representation);

            final Map<String, List<String>> attrs = new HashMap<>();
            attrs.put("tenant_id", List.of(tenantIdAttr));
            if (merchantIdAttr != null) {
                attrs.put("merchant_id", List.of(merchantIdAttr));
            }
            representation.setAttributes(attrs);

            if (props.provisioning().temporaryPassword()) {
                representation.setRequiredActions(List.of("UPDATE_PASSWORD"));
            }

            final CredentialRepresentation cred = new CredentialRepresentation();
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(props.provisioning().initialPassword());
            cred.setTemporary(props.provisioning().temporaryPassword());
            representation.setCredentials(List.of(cred));

            try (Response response = realm.users().create(representation)) {
                final int status = response.getStatus();
                if (status == Response.Status.CONFLICT.getStatusCode()) {
                    throw DomainException.with(new DomainError(
                            "Já existe um utilizador no Keycloak com o email indicado."));
                }
                if (status != Response.Status.CREATED.getStatusCode()) {
                    throw new IllegalStateException(
                            "Keycloak create user falhou: HTTP " + status + " — " + safeBody(response));
                }
                final String kcUserId = CreatedResponseUtil.getCreatedId(response);
                if (kcUserId == null || kcUserId.isBlank()) {
                    throw new IllegalStateException("Keycloak não devolveu o id do utilizador criado.");
                }

                try {
                    assignClientRole(realm, kcUserId, roleName);
                } catch (final RuntimeException e) {
                    try {
                        realm.users().delete(kcUserId);
                    } catch (final Exception cleanup) {
                        log.warn("Falha ao remover utilizador Keycloak {} após erro ao atribuir role: {}", kcUserId, cleanup.getMessage());
                    }
                    throw e;
                }
                return kcUserId;
            }
        }
    }

    public void deleteUserById(final String keycloakUserId) {
        if (keycloakUserId == null || keycloakUserId.isBlank()) {
            return;
        }
        try (Keycloak keycloak = buildKeycloak()) {
            final RealmResource realm = keycloak.realm(props.realm());
            try {
                realm.users().delete(keycloakUserId);
            } catch (final jakarta.ws.rs.NotFoundException e) {
                log.debug("Keycloak user {} já inexistente ao compensar.", keycloakUserId);
            }
        }
    }

    private void validateAdminConfig() {
        if (props.serverUrl() == null || props.serverUrl().isBlank()) {
            throw DomainException.with(new DomainError("app.keycloak.admin.server-url não configurado."));
        }
        if (props.clientSecret() == null || props.clientSecret().isBlank()) {
            throw DomainException.with(new DomainError("KEYCLOAK_ADMIN_CLIENT_SECRET / app.keycloak.admin.client-secret é obrigatório."));
        }
        final var pwd = props.provisioning().initialPassword();
        if (pwd == null || pwd.isBlank()) {
            throw DomainException.with(new DomainError(
                    "KEYCLOAK_USER_INITIAL_PASSWORD / app.keycloak.admin.provisioning.initial-password é obrigatório."));
        }
    }

    private Keycloak buildKeycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(trimTrailingSlash(props.serverUrl()))
                .realm(props.realm())
                .clientId(props.clientId())
                .clientSecret(props.clientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    private static String trimTrailingSlash(final String url) {
        if (url == null) {
            return null;
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private void assignClientRole(final RealmResource realm, final String kcUserId, final String roleName) {
        final var clients = realm.clients().findByClientId(props.rolesClientId());
        if (clients == null || clients.isEmpty()) {
            throw new IllegalStateException("Client OIDC não encontrado no realm: " + props.rolesClientId());
        }
        final String internalClientId = clients.getFirst().getId();
        final RoleRepresentation role = realm.clients().get(internalClientId).roles().get(roleName).toRepresentation();
        final var roleList = new ArrayList<RoleRepresentation>();
        roleList.add(role);
        realm.users().get(kcUserId).roles().clientLevel(internalClientId).add(roleList);
    }

    private static void applyNameParts(final String fullName, final UserRepresentation representation) {
        if (fullName == null || fullName.isBlank()) {
            representation.setFirstName("-");
            representation.setLastName("-");
            return;
        }
        final var parts = fullName.trim().split("\\s+", 2);
        representation.setFirstName(parts[0]);
        representation.setLastName(parts.length > 1 ? parts[1] : parts[0]);
    }

    private static String safeBody(final Response response) {
        try {
            if (response.hasEntity()) {
                return response.readEntity(String.class);
            }
        } catch (Exception ignored) {
        }
        return "";
    }
}
