package com.vp.core.application.publiclanding;

import com.vp.core.domain.campaign.CampaignStatus;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.infrastructure.api.response.PublicCampaignLandingResponse;
import com.vp.core.infrastructure.api.response.PublicCampaignLandingResponse.PublicCampaignPayload;
import com.vp.core.infrastructure.api.response.PublicCampaignLandingResponse.PublicCampaignStoreResponse;
import com.vp.core.infrastructure.api.response.PublicCampaignLandingResponse.PublicCampaignTenantBranding;
import com.vp.core.infrastructure.campaign.persistence.CampaignJpaRepository;
import com.vp.core.infrastructure.merchant.persistence.MerchantJpaRepository;
import com.vp.core.infrastructure.tenant.persistence.TenantJpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetPublicCampaignLandingUseCase {

    private final CampaignJpaRepository campaignJpaRepository;
    private final TenantJpaRepository tenantJpaRepository;
    private final MerchantJpaRepository merchantJpaRepository;

    public GetPublicCampaignLandingUseCase(
            final CampaignJpaRepository campaignJpaRepository,
            final TenantJpaRepository tenantJpaRepository,
            final MerchantJpaRepository merchantJpaRepository
    ) {
        this.campaignJpaRepository = campaignJpaRepository;
        this.tenantJpaRepository = tenantJpaRepository;
        this.merchantJpaRepository = merchantJpaRepository;
    }

    public PublicCampaignLandingResponse execute(final UUID tenantId, final UUID campaignId) {
        final var campaignOpt = campaignJpaRepository.findByTenantIdAndId(tenantId, campaignId);
        if (campaignOpt.isEmpty()) {
            throw NotFoundException.withMessage("Campanha não encontrada.");
        }
        final var camp = campaignOpt.get().toAggregate();
        if (camp.status() == CampaignStatus.DRAFT) {
            throw NotFoundException.withMessage("Campanha não encontrada.");
        }

        final var tenant = tenantJpaRepository.findById(tenantId)
                .orElseThrow(() -> NotFoundException.withMessage("Shopping não encontrado."))
                .toAggregate();

        final String displayName = tenant.getFantasyName() != null && !tenant.getFantasyName().isBlank()
                ? tenant.getFantasyName()
                : tenant.getName();

        final var networkUuid = UUID.fromString(camp.networkId().getValue());

        final var stores = merchantJpaRepository
                .findPublicStoresForNetwork(tenantId, networkUuid)
                .stream()
                .map(row -> new PublicCampaignStoreResponse(
                        row.getId().toString(),
                        row.getFantasyName() != null && !row.getFantasyName().isBlank()
                                ? row.getFantasyName()
                                : row.getName(),
                        row.getLandingLogoUrl(),
                        row.getCity()
                ))
                .toList();

        final var payload = new PublicCampaignPayload(
                camp.getId().getValue(),
                camp.name(),
                camp.status().name(),
                camp.startsAt(),
                camp.endsAt(),
                camp.expirationDays(),
                camp.bannerUrl()
        );

        final var branding = new PublicCampaignTenantBranding(
                displayName,
                tenant.getPrimaryBrandColor(),
                tenant.getSecondaryBrandColor()
        );

        return new PublicCampaignLandingResponse(branding, payload, stores);
    }
}
