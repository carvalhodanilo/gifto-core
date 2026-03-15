package com.vp.core.application.merchant.activate;

public record ActivateMerchantCommand(String tenantId, String merchantId) {
}