package com.vp.core.application.merchant.updateAccount;

public record UpdateMerchantBankAccountOutput(String merchantId, String tenantId) {
    public static UpdateMerchantBankAccountOutput of(final String merchantId, final String tenantId) {
        return new UpdateMerchantBankAccountOutput(merchantId, tenantId);
    }
}