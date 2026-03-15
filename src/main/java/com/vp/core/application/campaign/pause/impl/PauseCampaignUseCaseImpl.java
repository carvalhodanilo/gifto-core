package com.vp.core.application.campaign.pause.impl;

import com.vp.core.application.campaign.pause.PauseCampaignCommand;
import com.vp.core.application.campaign.pause.PauseCampaignUseCase;
import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PauseCampaignUseCaseImpl extends PauseCampaignUseCase {

    private final CampaignGateway campaignGateway;

    public PauseCampaignUseCaseImpl(final CampaignGateway campaignGateway) {
        this.campaignGateway = campaignGateway;
    }

    @Override
    @Transactional
    public void execute(final PauseCampaignCommand command) {
        final var campaignId = CampaignId.from(command.campaignId());
        final var tenantId = TenantId.from(command.tenantId());

        final var campaign = campaignGateway.findByTenantIdAndId(tenantId, campaignId)
                .orElseThrow(() -> NotFoundException.with(Campaign.class, campaignId));

        campaign.pause();
        campaignGateway.update(campaign);
    }
}