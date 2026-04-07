package com.vp.core.application.campaign.create.impl;

import com.vp.core.application.campaign.create.CreateCampaignCommand;
import com.vp.core.application.campaign.create.CreateCampaignOutput;
import com.vp.core.application.campaign.create.CreateCampaignUseCase;
import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.gateway.NetworkGateway;
import com.vp.core.domain.network.Network;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CreateCampaignUseCaseImpl extends CreateCampaignUseCase {

    private final CampaignGateway campaignGateway;
    private final NetworkGateway networkGateway;

    public CreateCampaignUseCaseImpl(
            final CampaignGateway campaignGateway,
            final NetworkGateway networkGateway
    ) {
        this.campaignGateway = campaignGateway;
        this.networkGateway = networkGateway;
    }

    @Override
    @Transactional
    public CreateCampaignOutput execute(final CreateCampaignCommand command) {
        final var tenantId = TenantId.from(command.tenantId());

        final var defaultNetwork = networkGateway.findDefaultByTenantId(tenantId)
                .orElseThrow(() -> NotFoundException.with(Network.class, tenantId));

        final var startsAt = parseInstant(command.startsAt());
        final var endsAt = parseInstant(command.endsAt());

        final var external = normalizeExternalUrl(command.externalLandingUrl());

        final var campaign = Campaign.create(
                tenantId,
                defaultNetwork.getId(),
                command.name(),
                command.expirationDays(),
                startsAt,
                endsAt,
                external
        );

        campaignGateway.create(campaign);

        return CreateCampaignOutput.of(campaign.getId().getValue());
    }

    private Instant parseInstant(final String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return Instant.parse(raw);
    }

    private static String normalizeExternalUrl(final String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return raw.trim();
    }
}
