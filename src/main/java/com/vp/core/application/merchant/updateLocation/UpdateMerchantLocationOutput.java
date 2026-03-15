package com.vp.core.application.merchant.updateLocation;

public record UpdateMerchantLocationOutput(String merchantId, String tenantId) {
    public static UpdateMerchantLocationOutput of(final String merchantId, final String tenantId) {
        return new UpdateMerchantLocationOutput(merchantId, tenantId);
    }
}