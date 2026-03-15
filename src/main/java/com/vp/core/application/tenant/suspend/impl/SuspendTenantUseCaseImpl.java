package com.vp.core.application.tenant.suspend.impl;

import com.vp.core.application.tenant.suspend.SuspendTenantCommand;
import com.vp.core.application.tenant.suspend.SuspendTenantUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.transaction.annotation.Transactional;

public class SuspendTenantUseCaseImpl extends SuspendTenantUseCase {

    private final TenantGateway tenantGateway;

    public SuspendTenantUseCaseImpl(final TenantGateway tenantGateway) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    @Transactional
    public void execute(final SuspendTenantCommand command) {
        final var tenantId = TenantId.from(command.tenantId());

        final var tenant = tenantGateway.findById(tenantId)
                .orElseThrow(() -> NotFoundException.with(Tenant.class, tenantId));

        tenant.suspend();

        tenantGateway.update(tenant);
    }
}