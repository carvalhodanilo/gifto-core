package com.vp.core.application.merchant.create;

public record CreateMerchantOutput(String merchantId) {
    public static CreateMerchantOutput of(final String merchantId) {
        return new CreateMerchantOutput(merchantId);
    }
}