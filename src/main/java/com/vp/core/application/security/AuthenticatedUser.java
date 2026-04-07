package com.vp.core.application.security;

import com.vp.core.infrastructure.security.KeycloakRolesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Principal interno da aplicação (independente de JWT/Keycloak).
 *
 * Claims padrão (OIDC): sub, preferred_username, email.
 * Claims customizadas do projeto: tenant_id, merchant_id.
 */
public record AuthenticatedUser(
        String subject,
        String username,
        String email,
        String tenantId,
        String merchantId,
        Set<String> roles,
        Set<String> authorities
) {
    public static final String CLAIM_SUB = "sub";
    public static final String CLAIM_PREFERRED_USERNAME = "preferred_username";
    public static final String CLAIM_EMAIL = "email";

    // Custom claims do projeto
    public static final String CLAIM_TENANT_ID = "tenant_id";
    public static final String CLAIM_MERCHANT_ID = "merchant_id";

    public static AuthenticatedUser fromJwt(
            final Jwt jwt,
            final Set<String> roles,
            final Collection<? extends GrantedAuthority> authorities
    ) {
        final var subject = jwt.getSubject();
        final var username = jwt.getClaimAsString(CLAIM_PREFERRED_USERNAME);
        final var email = jwt.getClaimAsString(CLAIM_EMAIL);
        final var tenantId = jwt.getClaimAsString(CLAIM_TENANT_ID);
        final var merchantId = jwt.getClaimAsString(CLAIM_MERCHANT_ID);

        final var authorityNames = new LinkedHashSet<String>();
        if (authorities != null) {
            for (final var a : authorities) {
                if (a != null && a.getAuthority() != null && !a.getAuthority().isBlank()) {
                    authorityNames.add(a.getAuthority());
                }
            }
        }

        final var safeRoles = roles == null ? Set.<String>of() : Collections.unmodifiableSet(new LinkedHashSet<>(roles));
        final var safeAuthorities = Collections.unmodifiableSet(authorityNames);

        return new AuthenticatedUser(
                subject,
                username,
                email,
                tenantId,
                merchantId,
                safeRoles,
                safeAuthorities
        );
    }

    /** Quando o IdP não envia tenant_id/merchant_id, preenche a partir da fonte interna (ex.: tabela users). */
    public AuthenticatedUser withScopeIds(final String tenantId, final String merchantId) {
        return new AuthenticatedUser(
                subject,
                username,
                email,
                tenantId != null ? tenantId : this.tenantId,
                merchantId != null ? merchantId : this.merchantId,
                roles,
                authorities
        );
    }

    public boolean hasRole(final String role) {
        if (role == null || role.isBlank()) {
            return false;
        }
        return roles.contains(role) || authorities.contains(KeycloakRolesExtractor.toAuthorityName(role));
    }

    public boolean isSystemAdmin() {
        return hasRole("system_admin");
    }

    public boolean isTenantAdmin() {
        return hasRole("tenant_admin");
    }

    public boolean isTenantOperator() {
        return hasRole("tenant_operator");
    }

    public boolean isMerchantAdmin() {
        return hasRole("merchant_admin");
    }

    public boolean isMerchantOperator() {
        return hasRole("merchant_operator");
    }
}
