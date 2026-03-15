package com.vp.core.application.campaign.update;

public record UpdateCampaignCommand(
        String tenantId,
        String campaignId,
        String name,
        int expirationDays,
        String startsAt,   // ISO-8601 or null
        String endsAt      // ISO-8601 or null
) {
}