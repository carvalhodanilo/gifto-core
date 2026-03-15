package com.vp.core.infrastructure.api.request;

public record UpdateCampaignRequest(
        String name,
        int expirationDays,
        String startsAt,   // ISO-8601 ou null
        String endsAt
) {
}
