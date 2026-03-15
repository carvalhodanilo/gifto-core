package com.vp.core.application.campaign.create;

public record CreateCampaignCommand(
        String tenantId,
        String name,
        int expirationDays,
        String startsAt,   // ISO-8601 ou null
        String endsAt      // ISO-8601 ou null
) {
}