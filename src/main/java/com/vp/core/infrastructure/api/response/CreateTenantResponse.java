package com.vp.core.infrastructure.api.response;

import com.vp.core.application.tenant.create.CreateTenantOutput;

public record CreateTenantResponse(String tenantId) {
    public static CreateTenantResponse from(final CreateTenantOutput output) {
        return new CreateTenantResponse(output.tenantId());
    }
}
