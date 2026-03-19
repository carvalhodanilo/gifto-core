package com.vp.core.infrastructure.security;

import com.vp.core.application.security.AuthenticatedUser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

/**
 * Converte um JWT em Authentication usando um Principal interno ({@link AuthenticatedUser}).
 */
public final class JwtToAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String rolesClientId;

    public JwtToAuthenticationConverter(@Nullable final String rolesClientId) {
        this.rolesClientId = rolesClientId;
    }

    @Override
    public AbstractAuthenticationToken convert(final Jwt jwt) {
        final Set<String> roles = KeycloakRolesExtractor.extractAllRoles(jwt, rolesClientId);
        final var authorities = KeycloakRolesExtractor.toAuthorities(roles);

        final var principal = AuthenticatedUser.fromJwt(jwt, roles, authorities);

        final var authentication = new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
        authentication.setDetails(jwt);
        return authentication;
    }
}
