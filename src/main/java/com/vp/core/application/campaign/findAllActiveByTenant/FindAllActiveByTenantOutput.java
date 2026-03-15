package com.vp.core.application.campaign.findAllActiveByTenant;

import com.vp.core.domain.campaign.Campaign;

import java.util.List;

public record FindAllActiveByTenantOutput(
        List<CampaignOutput> campaignList
) {
    public static FindAllActiveByTenantOutput of(
            List<Campaign> campaigns
    ) {
        final var tenantOutputs = campaigns.stream()
                .map(CampaignOutput::of)
                .toList();

        return new FindAllActiveByTenantOutput(tenantOutputs);
    }

    record CampaignOutput(
            String id,
            String campaignName
    ) {
        public static CampaignOutput of(final Campaign campaign) {
            return new CampaignOutput(
                    campaign.getId().getValue(),
                    campaign.name()
            );
        }
    }
}