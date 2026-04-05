package com.vp.core.application.merchant.uploadLandingLogo.impl;

import com.vp.core.application.merchant.uploadLandingLogo.UploadMerchantLandingLogoCommand;
import com.vp.core.application.merchant.uploadLandingLogo.UploadMerchantLandingLogoUseCase;
import com.vp.core.application.storage.ImageAssetKind;
import com.vp.core.application.storage.ImageAssetValidator;
import com.vp.core.application.storage.StoredAssetPaths;
import com.vp.core.application.storage.UploadStoredAssetOutput;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.storage.ObjectStorageGateway;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@Service
public class UploadMerchantLandingLogoUseCaseImpl extends UploadMerchantLandingLogoUseCase {

    private final MerchantGateway merchantGateway;
    private final ObjectStorageGateway objectStorageGateway;
    private final ImageAssetValidator imageAssetValidator;

    public UploadMerchantLandingLogoUseCaseImpl(
            final MerchantGateway merchantGateway,
            final ObjectStorageGateway objectStorageGateway,
            final ImageAssetValidator imageAssetValidator
    ) {
        this.merchantGateway = merchantGateway;
        this.objectStorageGateway = objectStorageGateway;
        this.imageAssetValidator = imageAssetValidator;
    }

    @Override
    @Transactional
    public UploadStoredAssetOutput execute(final UploadMerchantLandingLogoCommand command) {
        final var tenantId = TenantId.from(command.tenantId());
        final var merchantId = MerchantId.from(command.merchantId());
        final var merchant = merchantGateway.findByIdAndTenantId(merchantId, tenantId)
                .orElseThrow(() -> NotFoundException.with(Merchant.class, merchantId));

        imageAssetValidator.validate(
                ImageAssetKind.MERCHANT_LANDING_LOGO,
                command.contentType(),
                command.content().length,
                command.content()
        );
        final var ext = ImageAssetValidator.extensionForMime(command.contentType());
        final var key = StoredAssetPaths.merchantLandingLogoKey(merchantId.getValue(), ext);
        final var url = objectStorageGateway.putPublicObject(
                key,
                new ByteArrayInputStream(command.content()),
                command.content().length,
                command.contentType()
        );
        merchantGateway.update(merchant.updateLandingLogoUrl(url));
        return new UploadStoredAssetOutput(url);
    }
}
