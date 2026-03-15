package com.vp.core.application.tenant.activate.impl;

import com.vp.core.application.tenant.activate.ActivateTenantCommand;
import com.vp.core.application.tenant.activate.ActivateTenantUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.transaction.annotation.Transactional;

public class ActivateTenantUseCaseImpl extends ActivateTenantUseCase {

    private final TenantGateway tenantGateway;

    public ActivateTenantUseCaseImpl(final TenantGateway tenantGateway) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    @Transactional
    public void execute(final ActivateTenantCommand command) {
        final var tenantId = TenantId.from(command.tenantId());

        final var tenant = tenantGateway.findById(tenantId)
                .orElseThrow(() -> NotFoundException.with(Tenant.class, tenantId));

        tenant.activate();

        tenantGateway.update(tenant);
    }
}