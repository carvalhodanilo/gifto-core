package com.vp.core.application.merchant.findAllActiveByTenant.impl;

import com.vp.core.application.merchant.findAllActiveByTenant.FindAllActiveByTenantCommand;
import com.vp.core.application.merchant.findAllActiveByTenant.FindAllActiveByTenantOutput;
import com.vp.core.application.merchant.findAllActiveByTenant.FindAllActiveByTenantUseCase;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindAllActiveMerchantsByTenantUseCaseImpl extends FindAllActiveByTenantUseCase {

    private final MerchantGateway merchantGateway;

    public FindAllActiveMerchantsByTenantUseCaseImpl(
            final MerchantGateway merchantGateway
    ) {
        this.merchantGateway = merchantGateway;
    }

    @Override
    @Transactional
    public FindAllActiveByTenantOutput execute(FindAllActiveByTenantCommand command) {
        return FindAllActiveByTenantOutput.of(merchantGateway.findAllActiveByTenantId(TenantId.from(command.tenantId())));
    }
}
