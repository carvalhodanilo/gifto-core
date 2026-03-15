package com.vp.core.infrastructure.api.request;

public record CreateCampaignRequest(
        String name,
        int expirationDays,
        String startsAt,   // ISO-8601 ou null ou Instant? qual é melhor?
        String endsAt
) {
}
