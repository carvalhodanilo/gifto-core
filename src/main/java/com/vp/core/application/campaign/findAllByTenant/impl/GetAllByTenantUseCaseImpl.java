package com.vp.core.application.campaign.findAllByTenant.impl;

import com.vp.core.application.campaign.findAllByTenant.GetAllByTenantUseCase;
import com.vp.core.application.campaign.findAllByTenant.GetAllByTenantCommand;
import com.vp.core.application.campaign.findAllByTenant.GetAllByTenantOutput;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllByTenantUseCaseImpl extends GetAllByTenantUseCase {

    private final CampaignGateway campaignGateway;

    public GetAllByTenantUseCaseImpl(
            final CampaignGateway campaignGateway
    ) {
        this.campaignGateway = campaignGateway;
    }

    @Override
    @Transactional
    public GetAllByTenantOutput execute(GetAllByTenantCommand command) {
        return GetAllByTenantOutput.of(campaignGateway.findAllByTenantId(TenantId.from(command.tenantId())));
    }
}
