package com.vp.core.infrastructure.api.request;

public record UpdateCampaignRequest(
        String name,
        int expirationDays,
        String startsAt,
        String endsAt,
        String externalLandingUrl
) {
}
