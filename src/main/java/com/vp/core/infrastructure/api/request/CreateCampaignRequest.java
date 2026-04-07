package com.vp.core.infrastructure.api.request;

public record CreateCampaignRequest(
        String name,
        int expirationDays,
        String startsAt,
        String endsAt,
        String externalLandingUrl
) {
}
