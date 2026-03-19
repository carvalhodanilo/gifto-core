package com.vp.core.application.merchant.activate.impl;

import com.vp.core.application.merchant.activate.ActivateMerchantCommand;
import com.vp.core.application.merchant.activate.ActivateMerchantUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivateMerchantUseCaseImpl extends ActivateMerchantUseCase {

    private final MerchantGateway merchantGateway;

    public ActivateMerchantUseCaseImpl(final MerchantGateway merchantGateway) {
        this.merchantGateway = merchantGateway;
    }

    @Override
    @Transactional
    public void execute(final ActivateMerchantCommand command) {
        final var merchantId = MerchantId.from(command.merchantId());
        final var tenantId = TenantId.from(command.tenantId());

        final var merchant = merchantGateway.findByIdAndTenantId(merchantId, tenantId)
                .orElseThrow(() -> NotFoundException.with(Merchant.class, merchantId));

        merchant.activate();
        merchantGateway.update(merchant);
    }
}