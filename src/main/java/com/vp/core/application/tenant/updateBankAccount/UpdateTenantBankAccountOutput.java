package com.vp.core.application.tenant.updateBankAccount;

public record UpdateTenantBankAccountOutput(String tenantId) {
    public static UpdateTenantBankAccountOutput of(final String tenantId) {
        return new UpdateTenantBankAccountOutput(tenantId);
    }
}
