package com.vp.core.infrastructure.api.response;

import java.time.Instant;
import java.util.List;

public record PublicCampaignLandingResponse(
        PublicCampaignTenantBranding tenant,
        PublicCampaignPayload campaign,
        List<PublicCampaignStoreResponse> stores
) {
    public record PublicCampaignTenantBranding(
            String displayName,
            String logoUrl,
            String primaryBrandColor,
            String secondaryBrandColor
    ) {
    }

    public record PublicCampaignPayload(
            String id,
            String name,
            String status,
            Instant startsAt,
            Instant endsAt,
            int expirationDays,
            String bannerUrl
    ) {
    }

    public record PublicCampaignStoreResponse(
            String merchantId,
            String displayName,
            String landingLogoUrl,
            String city
    ) {
    }
}
