package com.vp.core.application.campaign.create;

public record CreateCampaignCommand(
        String tenantId,
        String name,
        int expirationDays,
        String startsAt,
        String endsAt,
        String externalLandingUrl
) {
}