package com.vp.core.application.tenant.getAll.impl;

import com.vp.core.application.tenant.getAll.GetAllTenantsOutput;
import com.vp.core.application.tenant.getAll.GetAllTenantsUseCase;
import com.vp.core.domain.gateway.TenantGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAllTenantsUseCaseImpl extends GetAllTenantsUseCase {

    private final TenantGateway tenantGateway;

    public GetAllTenantsUseCaseImpl(
            final TenantGateway tenantGateway
    ) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    @Transactional
    public GetAllTenantsOutput execute() {
        return GetAllTenantsOutput.of(tenantGateway.findAllActive());
    }
}
