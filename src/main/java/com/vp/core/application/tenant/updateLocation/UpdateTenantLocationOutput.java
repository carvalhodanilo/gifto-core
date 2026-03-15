package com.vp.core.application.tenant.updateLocation;

public record UpdateTenantLocationOutput(String tenantId) {
    public static UpdateTenantLocationOutput of(final String tenantId) {
        return new UpdateTenantLocationOutput(tenantId);
    }
}