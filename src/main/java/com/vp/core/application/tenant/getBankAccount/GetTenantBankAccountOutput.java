package com.vp.core.application.tenant.getBankAccount;

public record GetTenantBankAccountOutput(
        String tenantId,
        String bankCode,
        String bankName,
        String branch,
        String accountNumber,
        String accountDigit,
        String accountType,
        String holderName,
        String holderDocument,
        String pixKeyType,
        String pixKeyValue
) {

    public static GetTenantBankAccountOutput of(
            final String tenantId,
            final String bankCode,
            final String bankName,
            final String branch,
            final String accountNumber,
            final String accountDigit,
            final String accountType,
            final String holderName,
            final String holderDocument,
            final String pixKeyType,
            final String pixKeyValue
    ) {
        return new GetTenantBankAccountOutput(
                tenantId,
                bankCode,
                bankName,
                branch,
                accountNumber,
                accountDigit,
                accountType,
                holderName,
                holderDocument,
                pixKeyType,
                pixKeyValue
        );
    }
}
