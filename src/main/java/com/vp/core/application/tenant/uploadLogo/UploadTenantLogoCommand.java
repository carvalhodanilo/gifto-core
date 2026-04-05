package com.vp.core.application.tenant.uploadLogo;

public record UploadTenantLogoCommand(String tenantId, byte[] content, String contentType) {
}
