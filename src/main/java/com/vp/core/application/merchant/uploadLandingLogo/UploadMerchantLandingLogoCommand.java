package com.vp.core.application.merchant.uploadLandingLogo;

public record UploadMerchantLandingLogoCommand(String tenantId, String merchantId, byte[] content, String contentType) {
}
