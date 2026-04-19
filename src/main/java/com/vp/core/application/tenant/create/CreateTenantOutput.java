package com.vp.core.application.tenant.create;

public record CreateTenantOutput(
        String tenantId,
        String networkId,
        String adminUserId
) {
    public static CreateTenantOutput of(
            final String tenantId,
            final String networkId,
//            final String campaignId,
            final String adminUserId
    ) {
        return new CreateTenantOutput(tenantId, networkId, adminUserId);
    }
}