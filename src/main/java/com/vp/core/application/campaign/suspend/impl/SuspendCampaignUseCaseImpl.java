package com.vp.core.application.campaign.suspend.impl;

import com.vp.core.application.campaign.suspend.SuspendCampaignCommand;
import com.vp.core.application.campaign.suspend.SuspendCampaignUseCase;
import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuspendCampaignUseCaseImpl extends SuspendCampaignUseCase {

    private final CampaignGateway campaignGateway;

    public SuspendCampaignUseCaseImpl(final CampaignGateway campaignGateway) {
        this.campaignGateway = campaignGateway;
    }

    @Override
    @Transactional
    public void execute(final SuspendCampaignCommand command) {
        final var campaignId = CampaignId.from(command.campaignId());
        final var tenantId = TenantId.from(command.tenantId());

        final var campaign = campaignGateway.findByTenantIdAndId(tenantId, campaignId)
                .orElseThrow(() -> NotFoundException.with(Campaign.class, campaignId));

        campaign.suspend();
        campaignGateway.update(campaign);
    }
}