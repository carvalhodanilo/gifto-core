package com.vp.core.application.tenant.updateLocation.impl;

import com.vp.core.application.tenant.updateLocation.UpdateTenantLocationCommand;
import com.vp.core.application.tenant.updateLocation.UpdateTenantLocationOutput;
import com.vp.core.application.tenant.updateLocation.UpdateTenantLocationUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.Location;
import org.springframework.transaction.annotation.Transactional;

public class UpdateTenantLocationUseCaseImpl extends UpdateTenantLocationUseCase {

    private final TenantGateway tenantGateway;

    public UpdateTenantLocationUseCaseImpl(final TenantGateway tenantGateway) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    @Transactional
    public UpdateTenantLocationOutput execute(final UpdateTenantLocationCommand command) {
        final var tenantId = TenantId.from(command.tenantId());

        final var tenant = tenantGateway.findById(tenantId)
                .orElseThrow(() -> NotFoundException.with(Tenant.class, tenantId));

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
        tenantGateway.update(tenant);

        return UpdateTenantLocationOutput.of(tenantId.getValue());
    }
}