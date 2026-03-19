package com.vp.core.application.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class ScopeAuthorizer {

    private final CurrentUserProvider currentUserProvider;

    public ScopeAuthorizer(final CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    public boolean canAccessTenant(final String tenantId) {
        final var user = currentUserProvider.getCurrentUser().orElse(null);
        if (user == null) {
            return false;
        }
        if (user.isSystemAdmin()) {
            return true;
        }
        if (user.isTenantAdmin()) {
            return tenantId != null && tenantId.equals(user.tenantId());
        }
        return false;
    }

    public boolean canAccessMerchant(final String merchantId) {
        final var user = currentUserProvider.getCurrentUser().orElse(null);
        if (user == null) {
            return false;
        }
        if (user.isSystemAdmin()) {
            return true;
        }
        if (user.isMerchantAdmin() || user.isMerchantOperator()) {
            return merchantId != null && merchantId.equals(user.merchantId());
        }
        return false;
    }

    public void ensureTenantAccess(final String tenantId) {
        if (!canAccessTenant(tenantId)) {
            throw new AccessDeniedException("Acesso negado ao tenant informado.");
        }
    }

    public void ensureMerchantAccess(final String merchantId) {
        if (!canAccessMerchant(merchantId)) {
            throw new AccessDeniedException("Acesso negado ao merchant informado.");
        }
    }
}
