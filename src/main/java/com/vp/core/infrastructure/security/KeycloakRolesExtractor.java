package com.vp.core.infrastructure.security;

import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Extrai roles do Keycloak em formatos comuns:
 * - realm_access.roles
 * - resource_access.{client}.roles
 *
 * Null-safe e resiliente a blocos ausentes.
 */
public final class KeycloakRolesExtractor {

    public static final String CLAIM_REALM_ACCESS = "realm_access";
    public static final String CLAIM_RESOURCE_ACCESS = "resource_access";
    public static final String CLAIM_ROLES = "roles";

    private KeycloakRolesExtractor() {
    }

    public static Set<String> extractRealmRoles(final Jwt jwt) {
        final var realmAccess = jwt.getClaimAsMap(CLAIM_REALM_ACCESS);
        return extractRolesFromAccessBlock(realmAccess);
    }

    public static Set<String> extractClientRoles(final Jwt jwt, @Nullable final String clientId) {
        if (clientId == null || clientId.isBlank()) {
            return Collections.emptySet();
        }

        final var resourceAccess = jwt.getClaimAsMap(CLAIM_RESOURCE_ACCESS);
        if (resourceAccess == null || resourceAccess.isEmpty()) {
            return Collections.emptySet();
        }

        final Object clientBlock = resourceAccess.get(clientId);
        if (!(clientBlock instanceof Map<?, ?> clientMap)) {
            return Collections.emptySet();
        }

        @SuppressWarnings("unchecked")
        final Map<String, Object> accessBlock = (Map<String, Object>) clientMap;
        return extractRolesFromAccessBlock(accessBlock);
    }

    public static Set<String> extractAllRoles(final Jwt jwt, @Nullable final String clientId) {
        final var roles = new LinkedHashSet<String>();
        roles.addAll(extractRealmRoles(jwt));
        roles.addAll(extractClientRoles(jwt, clientId));
        return Collections.unmodifiableSet(roles);
    }

    public static Set<GrantedAuthority> toAuthorities(final Collection<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }

        final var authorities = new LinkedHashSet<GrantedAuthority>();
        for (final var role : roles) {
            if (role == null || role.isBlank()) {
                continue;
            }
            authorities.add(new SimpleGrantedAuthority(toAuthorityName(role)));
        }
        return Collections.unmodifiableSet(authorities);
    }

    public static String toAuthorityName(final String role) {
        return "ROLE_" + role.trim();
    }

    private static Set<String> extractRolesFromAccessBlock(@Nullable final Map<String, Object> accessBlock) {
        if (accessBlock == null || accessBlock.isEmpty()) {
            return Collections.emptySet();
        }

        final Object rolesObj = accessBlock.get(CLAIM_ROLES);
        if (!(rolesObj instanceof Collection<?> rolesCollection)) {
            return Collections.emptySet();
        }

        final var roles = new LinkedHashSet<String>();
        for (final Object r : rolesCollection) {
            if (r == null) {
                continue;
            }
            final var role = Objects.toString(r, "").trim();
            if (!role.isBlank()) {
                roles.add(role);
            }
        }
        return Collections.unmodifiableSet(roles);
    }
}
