package com.vp.core.application.campaign.update;

public record UpdateCampaignCommand(
        String tenantId,
        String campaignId,
        String name,
        int expirationDays,
        String startsAt,
        String endsAt,
        String externalLandingUrl
) {
}