package com.vp.core.application.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserProvider {

    public Optional<AuthenticatedUser> getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        final Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser user) {
            return Optional.of(user);
        }

        // Fallback: caso algum fluxo deixe principal como Jwt.
        if (principal instanceof Jwt jwt) {
            return Optional.of(AuthenticatedUser.fromJwt(jwt, null, authentication.getAuthorities()));
        }

        return Optional.empty();
    }

    public AuthenticatedUser getCurrentUserOrThrow() {
        return getCurrentUser().orElseThrow(() -> new AccessDeniedException("Não autenticado."));
    }

    public String getCurrentTenantId() {
        return getCurrentUser().map(AuthenticatedUser::tenantId).orElse(null);
    }

    public String getCurrentMerchantId() {
        return getCurrentUser().map(AuthenticatedUser::merchantId).orElse(null);
    }

    public boolean hasRole(final String role) {
        return getCurrentUser().map(u -> u.hasRole(role)).orElse(false);
    }

    public boolean isSystemAdmin() {
        return getCurrentUser().map(AuthenticatedUser::isSystemAdmin).orElse(false);
    }

    public boolean isTenantAdmin() {
        return getCurrentUser().map(AuthenticatedUser::isTenantAdmin).orElse(false);
    }

    public boolean isTenantOperator() {
        return getCurrentUser().map(AuthenticatedUser::isTenantOperator).orElse(false);
    }

    public boolean isMerchantAdmin() {
        return getCurrentUser().map(AuthenticatedUser::isMerchantAdmin).orElse(false);
    }

    public boolean isMerchantOperator() {
        return getCurrentUser().map(AuthenticatedUser::isMerchantOperator).orElse(false);
    }
}
