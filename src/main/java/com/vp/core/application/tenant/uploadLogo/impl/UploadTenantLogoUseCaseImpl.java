package com.vp.core.application.tenant.uploadLogo.impl;

import com.vp.core.application.storage.ImageAssetKind;
import com.vp.core.application.storage.ImageAssetValidator;
import com.vp.core.application.storage.StoredAssetPaths;
import com.vp.core.application.storage.UploadStoredAssetOutput;
import com.vp.core.application.tenant.uploadLogo.UploadTenantLogoCommand;
import com.vp.core.application.tenant.uploadLogo.UploadTenantLogoUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.storage.ObjectStorageGateway;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@Service
public class UploadTenantLogoUseCaseImpl extends UploadTenantLogoUseCase {

    private final TenantGateway tenantGateway;
    private final ObjectStorageGateway objectStorageGateway;
    private final ImageAssetValidator imageAssetValidator;

    public UploadTenantLogoUseCaseImpl(
            final TenantGateway tenantGateway,
            final ObjectStorageGateway objectStorageGateway,
            final ImageAssetValidator imageAssetValidator
    ) {
        this.tenantGateway = tenantGateway;
        this.objectStorageGateway = objectStorageGateway;
        this.imageAssetValidator = imageAssetValidator;
    }

    @Override
    @Transactional
    public UploadStoredAssetOutput execute(final UploadTenantLogoCommand command) {
        final var tenantId = TenantId.from(command.tenantId());
        final var tenant = tenantGateway.findById(tenantId)
                .orElseThrow(() -> NotFoundException.with(Tenant.class, tenantId));

        imageAssetValidator.validate(
                ImageAssetKind.TENANT_LOGO,
                command.contentType(),
                command.content().length,
                command.content()
        );
        final var ext = ImageAssetValidator.extensionForMime(command.contentType());
        final var key = StoredAssetPaths.tenantLogoKey(tenantId.getValue(), ext);
        final var url = objectStorageGateway.putPublicObject(
                key,
                new ByteArrayInputStream(command.content()),
                command.content().length,
                command.contentType()
        );
        tenantGateway.update(tenant.updateLogoUrl(url));
        return new UploadStoredAssetOutput(url);
    }
}
