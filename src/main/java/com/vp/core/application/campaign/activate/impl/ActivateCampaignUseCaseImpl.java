package com.vp.core.application.campaign.activate.impl;

import com.vp.core.application.campaign.activate.ActivateCampaignCommand;
import com.vp.core.application.campaign.activate.ActivateCampaignUseCase;
import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.exceptions.DomainException;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.utils.InstantUtils;
import com.vp.core.domain.validation.DomainError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivateCampaignUseCaseImpl extends ActivateCampaignUseCase {

    private final CampaignGateway campaignGateway;

    public ActivateCampaignUseCaseImpl(final CampaignGateway campaignGateway) {
        this.campaignGateway = campaignGateway;
    }

    @Override
    @Transactional
    public void execute(final ActivateCampaignCommand command) {
        final var campaignId = CampaignId.from(command.campaignId());
        final var tenantId = TenantId.from(command.tenantId());

        final var campaign = campaignGateway.findByTenantIdAndId(tenantId, campaignId)
                .orElseThrow(() -> NotFoundException.with(Campaign.class, campaignId));

        final var now = InstantUtils.now();
        if (now.isBefore(campaign.startsAt())) {
            throw DomainException.with(new DomainError(
                    "Não é possível ativar a campanha antes da data de início."));
        }
        if (now.isAfter(campaign.endsAt())) {
            throw DomainException.with(new DomainError(
                    "Não é possível ativar a campanha após a data de término."));
        }

        campaign.activate();
        campaignGateway.update(campaign);
    }
}