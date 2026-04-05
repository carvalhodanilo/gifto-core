package com.vp.core.application.campaign.uploadBanner.impl;

import com.vp.core.application.campaign.uploadBanner.UploadCampaignBannerCommand;
import com.vp.core.application.campaign.uploadBanner.UploadCampaignBannerUseCase;
import com.vp.core.application.storage.ImageAssetKind;
import com.vp.core.application.storage.ImageAssetValidator;
import com.vp.core.application.storage.StoredAssetPaths;
import com.vp.core.application.storage.UploadStoredAssetOutput;
import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.storage.ObjectStorageGateway;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@Service
public class UploadCampaignBannerUseCaseImpl extends UploadCampaignBannerUseCase {

    private final CampaignGateway campaignGateway;
    private final ObjectStorageGateway objectStorageGateway;
    private final ImageAssetValidator imageAssetValidator;

    public UploadCampaignBannerUseCaseImpl(
            final CampaignGateway campaignGateway,
            final ObjectStorageGateway objectStorageGateway,
            final ImageAssetValidator imageAssetValidator
    ) {
        this.campaignGateway = campaignGateway;
        this.objectStorageGateway = objectStorageGateway;
        this.imageAssetValidator = imageAssetValidator;
    }

    @Override
    @Transactional
    public UploadStoredAssetOutput execute(final UploadCampaignBannerCommand command) {
        final var tenantId = TenantId.from(command.tenantId());
        final var campaignId = CampaignId.from(command.campaignId());
        final var campaign = campaignGateway.findByTenantIdAndId(tenantId, campaignId)
                .orElseThrow(() -> NotFoundException.with(Campaign.class, campaignId));

        imageAssetValidator.validate(
                ImageAssetKind.CAMPAIGN_BANNER,
                command.contentType(),
                command.content().length,
                command.content()
        );
        final var ext = ImageAssetValidator.extensionForMime(command.contentType());
        final var key = StoredAssetPaths.campaignBannerKey(campaignId.getValue(), ext);
        final var url = objectStorageGateway.putPublicObject(
                key,
                new ByteArrayInputStream(command.content()),
                command.content().length,
                command.contentType()
        );
        campaignGateway.update(campaign.updateBannerUrl(url));
        return new UploadStoredAssetOutput(url);
    }
}
