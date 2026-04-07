package com.vp.core.application.campaign.get.impl;

import com.vp.core.application.campaign.findAllByTenant.GetAllByTenantOutput;
import com.vp.core.application.campaign.get.GetCampaignCommand;
import com.vp.core.application.campaign.get.GetCampaignUseCase;
import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;

@Service
public class GetCampaignUseCaseImpl extends GetCampaignUseCase {

    private final CampaignGateway campaignGateway;

    public GetCampaignUseCaseImpl(final CampaignGateway campaignGateway) {
        this.campaignGateway = campaignGateway;
    }

    @Override
    public GetAllByTenantOutput.GetCampaignOutput execute(final GetCampaignCommand command) {
        final var tenantId = TenantId.from(command.tenantId());
        final var campaignId = CampaignId.from(command.campaignId());

        final var campaign = campaignGateway.findByTenantIdAndId(tenantId, campaignId)
                .orElseThrow(() -> NotFoundException.with(Campaign.class, campaignId));

        return GetAllByTenantOutput.GetCampaignOutput.of(campaign);
    }
}
