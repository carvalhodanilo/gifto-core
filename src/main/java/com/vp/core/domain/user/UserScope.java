package com.vp.core.domain.user;

import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;

public final class UserScope {

    private final ScopeType type;
    private final String scopeId; // null for PLATFORM

    private UserScope(final ScopeType type, final String scopeId) {
        this.type = type;
        this.scopeId = scopeId;
    }

    public static UserScope platform() {
        return new UserScope(ScopeType.PLATFORM, null);
    }

    public static UserScope tenant(final TenantId tenantId) {
        return new UserScope(ScopeType.TENANT, tenantId.getValue());
    }

    public static UserScope merchant(final MerchantId merchantId) {
        return new UserScope(ScopeType.MERCHANT, merchantId.getValue());
    }

    public ScopeType getType() {
        return type;
    }

    public String getScopeId() {
        return scopeId;
    }

    public boolean isPlatform() {
        return type == ScopeType.PLATFORM;
    }

    public boolean isTenant() {
        return type == ScopeType.TENANT;
    }

    public boolean isMerchant() {
        return type == ScopeType.MERCHANT;
    }
}
