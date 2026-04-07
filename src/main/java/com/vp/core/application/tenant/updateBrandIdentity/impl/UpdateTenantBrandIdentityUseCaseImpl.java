package com.vp.core.application.tenant.updateBrandIdentity.impl;

import com.vp.core.application.tenant.update.UpdateTenantOutput;
import com.vp.core.application.tenant.updateBrandIdentity.UpdateTenantBrandIdentityCommand;
import com.vp.core.application.tenant.updateBrandIdentity.UpdateTenantBrandIdentityUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTenantBrandIdentityUseCaseImpl extends UpdateTenantBrandIdentityUseCase {

    private final TenantGateway tenantGateway;

    public UpdateTenantBrandIdentityUseCaseImpl(final TenantGateway tenantGateway) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    @Transactional
    public UpdateTenantOutput execute(final UpdateTenantBrandIdentityCommand command) {
        final var tenantId = TenantId.from(command.tenantId());
        final var tenant = tenantGateway.findById(tenantId)
                .orElseThrow(() -> NotFoundException.with(Tenant.class, tenantId));

        tenant.updateBrandColors(command.primaryColor(), command.secondaryColor());
        tenantGateway.update(tenant);

        return UpdateTenantOutput.of(tenantId.getValue());
    }
}
