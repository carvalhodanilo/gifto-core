package com.vp.core.application.merchant.suspend;

public record SuspendMerchantCommand(String tenantId, String merchantId) {
}