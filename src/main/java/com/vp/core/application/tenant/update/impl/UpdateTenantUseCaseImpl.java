package com.vp.core.application.tenant.update.impl;

import com.vp.core.application.tenant.update.UpdateTenantCommand;
import com.vp.core.application.tenant.update.UpdateTenantOutput;
import com.vp.core.application.tenant.update.UpdateTenantUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTenantUseCaseImpl extends UpdateTenantUseCase {

    private final TenantGateway tenantGateway;

    public UpdateTenantUseCaseImpl(final TenantGateway tenantGateway) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    @Transactional
    public UpdateTenantOutput execute(final UpdateTenantCommand command) {
        final var tenantId = TenantId.from(command.id());

        final var tenant = tenantGateway.findById(tenantId)
                .orElseThrow(() -> NotFoundException.with(Tenant.class, tenantId));

        tenant.updateProfile(
                command.legalName(),
                command.fantasyName(),
                command.phone1(),
                command.phone2(),
                Email.with(command.contactEmail()),
                command.url()
        );

        tenantGateway.update(tenant);

        return UpdateTenantOutput.of(tenantId.getValue());
    }
}