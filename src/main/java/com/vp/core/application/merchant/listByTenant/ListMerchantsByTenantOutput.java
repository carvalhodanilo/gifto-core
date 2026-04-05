package com.vp.core.application.merchant.listByTenant;

public record ListMerchantsByTenantOutput(
        String merchantId,
        String fantasyName,
        String landingLogoUrl,
        String status
) {
    public static ListMerchantsByTenantOutput of(
            final String merchantId,
            final String fantasyName,
            final String landingLogoUrl,
            final String status
    ) {
        return new ListMerchantsByTenantOutput(merchantId, fantasyName, landingLogoUrl, status);
    }
}
