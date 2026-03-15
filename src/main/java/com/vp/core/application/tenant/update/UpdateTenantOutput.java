package com.vp.core.application.tenant.update;

public record UpdateTenantOutput(String tenantId) {
    public static UpdateTenantOutput of(final String tenantId) {
        return new UpdateTenantOutput(tenantId);
    }
}