package com.vp.core.application.campaign.update.impl;

import com.vp.core.application.campaign.update.UpdateCampaignCommand;
import com.vp.core.application.campaign.update.UpdateCampaignOutput;
import com.vp.core.application.campaign.update.UpdateCampaignUseCase;
import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UpdateCampaignUseCaseImpl extends UpdateCampaignUseCase {

    private final CampaignGateway campaignGateway;

    public UpdateCampaignUseCaseImpl(final CampaignGateway campaignGateway) {
        this.campaignGateway = campaignGateway;
    }

    @Override
    @Transactional
    public UpdateCampaignOutput execute(final UpdateCampaignCommand command) {
        final var tenantId = TenantId.from(command.tenantId());
        final var campaignId = CampaignId.from(command.campaignId());

        final var campaign = campaignGateway.findByTenantIdAndId(tenantId, campaignId)
                .orElseThrow(() -> NotFoundException.with(Campaign.class, campaignId));

        final var startsAt = parseInstant(command.startsAt());
        final var endsAt = parseInstant(command.endsAt());

        campaign.update(
                command.name(),
                command.expirationDays(),
                startsAt,
                endsAt
        );

        campaignGateway.update(campaign);

        return UpdateCampaignOutput.of(campaignId.getValue());
    }

    private Instant parseInstant(final String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Instant.parse(raw);
    }
}
