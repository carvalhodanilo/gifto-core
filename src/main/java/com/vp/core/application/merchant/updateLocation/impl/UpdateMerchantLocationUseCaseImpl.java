package com.vp.core.application.merchant.updateLocation.impl;

import com.vp.core.application.merchant.updateLocation.UpdateMerchantLocationCommand;
import com.vp.core.application.merchant.updateLocation.UpdateMerchantLocationOutput;
import com.vp.core.application.merchant.updateLocation.UpdateMerchantLocationUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.Location;
import org.springframework.transaction.annotation.Transactional;

public class UpdateMerchantLocationUseCaseImpl extends UpdateMerchantLocationUseCase {

    private final MerchantGateway merchantGateway;

    public UpdateMerchantLocationUseCaseImpl(final MerchantGateway merchantGateway) {
        this.merchantGateway = merchantGateway;
    }

    @Override
    @Transactional
    public UpdateMerchantLocationOutput execute(final UpdateMerchantLocationCommand command) {
        final var merchantId = MerchantId.from(command.merchantId());
        final var tenantId = TenantId.from(command.tenantId());

        final var tenant = merchantGateway.findByIdAndTenantId(merchantId, tenantId)
                .orElseThrow(() -> NotFoundException.with(Merchant.class, merchantId));

        final var location = Location.with(
                command.street(),
                command.number(),
                command.neighborhood(),
                command.complement(),
                command.city(),
                command.state(),
                command.country(),
                command.postalCode()
        );

        tenant.updateLocation(location);
        merchantGateway.update(tenant);

        return UpdateMerchantLocationOutput.of(merchantId.getValue(), tenantId.getValue());
    }
}