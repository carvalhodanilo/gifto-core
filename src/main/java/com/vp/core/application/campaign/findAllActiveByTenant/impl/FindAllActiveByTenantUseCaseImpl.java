package com.vp.core.application.campaign.findAllActiveByTenant.impl;

import com.vp.core.application.campaign.findAllActiveByTenant.FindAllActiveByTenantCommand;
import com.vp.core.application.campaign.findAllActiveByTenant.FindAllActiveByTenantOutput;
import com.vp.core.application.campaign.findAllActiveByTenant.FindAllActiveByTenantUseCase;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindAllActiveByTenantUseCaseImpl extends FindAllActiveByTenantUseCase {

    private final CampaignGateway campaignGateway;

    public FindAllActiveByTenantUseCaseImpl(
            final CampaignGateway campaignGateway
    ) {
        this.campaignGateway = campaignGateway;
    }

    @Override
    @Transactional
    public FindAllActiveByTenantOutput execute(FindAllActiveByTenantCommand command) {
        return FindAllActiveByTenantOutput.of(campaignGateway.findAllActiveByTenantId(TenantId.from(command.tenantId())));
    }
}
