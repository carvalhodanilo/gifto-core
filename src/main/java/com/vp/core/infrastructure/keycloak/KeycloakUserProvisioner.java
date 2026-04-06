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
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.ProcessingException;
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
            final RealmResource realm = keycloak.realm(effectiveRealm());

            final var representation = new UserRepresentation();
            representation.setUsername(user.getEmail().getValue());
            representation.setEmail(user.getEmail().getValue());
            /*
             * MVP: conta ativa e email tratado como verificado (sem fluxo de confirmação).
             * Futuro: avaliar setEmailVerified(false) + required action VERIFY_EMAIL (e/ou email com link);
             *         ou desativar "login com email não confirmado" nas políticas do realm no Keycloak.
             */
            representation.setEnabled(true);
            representation.setEmailVerified(true);
            applyNameParts(user.getName(), representation);

            final Map<String, List<String>> attrs = new HashMap<>();
            attrs.put("tenant_id", List.of(tenantIdAttr));
            if (merchantIdAttr != null) {
                attrs.put("merchant_id", List.of(merchantIdAttr));
            }
            representation.setAttributes(attrs);

            /*
             * MVP: sem required actions (login direto com a senha inicial).
             * Futuro: com temporaryPassword=true, forçar troca no 1º acesso via UPDATE_PASSWORD
             *         (alinhar com cred.setTemporary(true) abaixo e políticas do realm).
             */
            if (props.provisioning().temporaryPassword()) {
                representation.setRequiredActions(List.of("UPDATE_PASSWORD"));
            }

            final CredentialRepresentation cred = new CredentialRepresentation();
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(props.provisioning().initialPassword());
            /* MVP: senha definitiva. Futuro: true + UPDATE_PASSWORD / email de boas-vindas. */
            cred.setTemporary(props.provisioning().temporaryPassword());
            representation.setCredentials(List.of(cred));

            try (Response response = realm.users().create(representation)) {
                final int status = response.getStatus();
                if (status == Response.Status.CONFLICT.getStatusCode()) {
                    throw DomainException.with(new DomainError(
                            "Já existe um utilizador no Keycloak com o email indicado."));
                }
                if (status == Response.Status.FORBIDDEN.getStatusCode()) {
                    throw DomainException.with(new DomainError(keycloakAdminForbiddenHint()));
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
        } catch (final ProcessingException e) {
            throw translateKeycloakFailure(e);
        } catch (final NotAuthorizedException e) {
            throw DomainException.with(new DomainError(keycloakAdminUnauthorizedHint()));
        } catch (final ForbiddenException e) {
            throw DomainException.with(new DomainError(keycloakAdminForbiddenHint()));
        }
    }

    public void deleteUserById(final String keycloakUserId) {
        if (keycloakUserId == null || keycloakUserId.isBlank()) {
            return;
        }
        try (Keycloak keycloak = buildKeycloak()) {
            final RealmResource realm = keycloak.realm(effectiveRealm());
            try {
                realm.users().delete(keycloakUserId);
            } catch (final jakarta.ws.rs.NotFoundException e) {
                log.debug("Keycloak user {} já inexistente ao compensar.", keycloakUserId);
            }
        } catch (final ProcessingException e) {
            if (causedByNotAuthorized(e)) {
                log.warn("Keycloak compensação: 401 na Admin API. {}", keycloakAdminUnauthorizedHint());
            } else {
                log.warn("Keycloak compensação: {}", e.getMessage(), e);
            }
        } catch (final NotAuthorizedException e) {
            log.warn("Keycloak compensação: 401 na Admin API. {}", keycloakAdminUnauthorizedHint());
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
                .realm(effectiveRealm())
                .clientId(effectiveAdminClientId())
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
        final String desiredClientId = effectiveRolesClientId();
        final String internalClientId = resolveInternalClientUuid(realm, desiredClientId, roleName);
        final RoleRepresentation role = realm.clients().get(internalClientId).roles().get(roleName).toRepresentation();
        final var roleList = new ArrayList<RoleRepresentation>();
        roleList.add(role);
        realm.users().get(kcUserId).roles().clientLevel(internalClientId).add(roleList);
    }

    /** Realm onde correm users/clients (não confundir com o token do master). String vazia no .env quebra o default do Spring. */
    private String effectiveRealm() {
        final String r = props.realm();
        if (r == null || r.isBlank()) {
            log.warn("KEYCLOAK_ADMIN_REALM / app.keycloak.admin.realm vazio — a usar 'gifto'.");
            return "gifto";
        }
        return r.trim();
    }

    private String effectiveAdminClientId() {
        final String id = props.clientId();
        if (id == null || id.isBlank()) {
            return "gifto-core-admin";
        }
        return id.trim();
    }

    private String effectiveRolesClientId() {
        final String id = props.rolesClientId();
        if (id == null || id.isBlank()) {
            return "voucher-platform-api";
        }
        return id.trim();
    }

    private String resolveInternalClientUuid(
            final RealmResource realm,
            final String desiredClientId,
            final String roleNameForHint
    ) {
        var found = realm.clients().findByClientId(desiredClientId);
        if (found != null && !found.isEmpty()) {
            return found.getFirst().getId();
        }
        int first = 0;
        final int max = 100;
        while (true) {
            final var page = realm.clients().findAll(null, Boolean.FALSE, null, first, max);
            if (page == null || page.isEmpty()) {
                break;
            }
            for (final var c : page) {
                if (desiredClientId.equals(c.getClientId())) {
                    return c.getId();
                }
            }
            if (page.size() < max) {
                break;
            }
            first += max;
        }
        throw DomainException.with(new DomainError(rolesClientMissingHint(desiredClientId, roleNameForHint, effectiveRealm())));
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

    private static RuntimeException translateKeycloakFailure(final Throwable e) {
        if (causedByNotAuthorized(e)) {
            return DomainException.with(new DomainError(keycloakAdminUnauthorizedHint()));
        }
        if (causedByForbidden(e)) {
            return DomainException.with(new DomainError(keycloakAdminForbiddenHint()));
        }
        if (e instanceof RuntimeException re) {
            return re;
        }
        return new IllegalStateException(e);
    }

    private static boolean causedByNotAuthorized(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof NotAuthorizedException) {
                return true;
            }
        }
        return false;
    }

    private static boolean causedByForbidden(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof ForbiddenException) {
                return true;
            }
        }
        return false;
    }

    private static String keycloakAdminUnauthorizedHint() {
        return "Keycloak Admin API devolveu 401 ao obter token (client credentials). "
                + "Confirme: (1) existe o client gifto-core-admin no realm gifto, com Client authentication ativa e Service accounts ON; "
                + "(2) KEYCLOAK_ADMIN_CLIENT_SECRET é exatamente o secret desse client (Admin Console → Clients → gifto-core-admin → Credentials); "
                + "(3) KEYCLOAK_ADMIN_SERVER_URL=http://keycloak:8080/auth quando o Keycloak usa KC_HTTP_RELATIVE_PATH=/auth; "
                + "(4) o service account tem roles realm-management (manage-users). Se o realm foi criado antes deste client, cria-o manualmente ou reimporta o realm.";
    }

    private static String keycloakAdminForbiddenHint() {
        return "Keycloak Admin API devolveu 403 (sem permissão). O separador \"Roles\" do client só define papéis desse client — não autoriza o backend. "
                + "Vai a Clients → gifto-core-admin → Service accounts roles → Assign role → "
                + "Filter by clients → realm-management e adiciona manage-users, view-users, query-users, view-clients e query-clients "
                + "(sem view-clients/query-clients a listagem de clients pode falhar). "
                + "Em Capability config o client precisa de \"Service accounts roles\" ativo.";
    }

    private static String rolesClientMissingHint(final String rolesClientId, final String roleName, final String realmUsed) {
        return "Não foi possível resolver o client \"" + rolesClientId + "\" no realm \"" + realmUsed + "\" "
                + "(KEYCLOAK_ADMIN_ROLES_CLIENT_ID / KEYCLOAK_ADMIN_REALM). "
                + "Se vês o client no Admin Console mas o backend falha: confirma o realm no canto superior esquerdo (tem de ser o mesmo) "
                + "e não deixes KEYCLOAK_ADMIN_REALM vazio no .env (o Spring aceita string vazia e ignorava o default). "
                + "No service account do gifto-core-admin adiciona em realm-management: view-clients e query-clients. "
                + "As roles da app (ex. " + roleName + ") vivem no client voucher-platform-api.";
    }
}
