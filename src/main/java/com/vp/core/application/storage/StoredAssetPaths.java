package com.vp.core.application.storage;

public final class StoredAssetPaths {

    private StoredAssetPaths() {
    }

    public static String tenantLogoKey(final String tenantId, final String extension) {
        return "public/tenant/" + tenantId + "/logo." + extension;
    }

    public static String campaignBannerKey(final String campaignId, final String extension) {
        return "public/campaign/" + campaignId + "/banner." + extension;
    }

    public static String merchantLandingLogoKey(final String merchantId, final String extension) {
        return "public/merchant/" + merchantId + "/logo." + extension;
    }
}
