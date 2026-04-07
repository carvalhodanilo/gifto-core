package com.vp.core.application.campaign.findAllByTenant;

import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignStatus;

import java.time.Instant;
import java.util.List;

public record GetAllByTenantOutput(
        List<GetCampaignOutput> campaignList
) {
    public static GetAllByTenantOutput of(
            List<Campaign> campaigns
    ) {
        final var tenantOutputs = campaigns.stream()
                .map(GetCampaignOutput::of)
                .toList();

        return new GetAllByTenantOutput(tenantOutputs);
    }

    public record GetCampaignOutput(
            String id,
            String name,
            int expirationDays,
            Instant startsAt,
            Instant endsAt,
            String bannerUrl,
            String externalLandingUrl,
            CampaignStatus status
    ) {
        public static GetCampaignOutput of(final Campaign campaign) {
            return new GetCampaignOutput(
                    campaign.getId().getValue(),
                    campaign.name(),
                    campaign.expirationDays(),
                    campaign.startsAt(),
                    campaign.endsAt(),
                    campaign.bannerUrl(),
                    campaign.externalLandingUrl(),
                    campaign.status()
            );
        }
    }
}