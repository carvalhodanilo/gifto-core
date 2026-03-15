package com.vp.core.application.merchant.update;

public record UpdateMerchantOutput(String merchantId) {
    public static UpdateMerchantOutput of(final String merchantId) {
        return new UpdateMerchantOutput(merchantId);
    }
}