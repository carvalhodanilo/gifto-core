package com.vp.core.application.tenant.get.impl;

import com.vp.core.application.tenant.get.GetTenantOutput;
import com.vp.core.application.tenant.get.GetTenantUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;

@Service
public class GetTenantUseCaseImpl extends GetTenantUseCase {

    private final TenantGateway tenantGateway;

    public GetTenantUseCaseImpl(final TenantGateway tenantGateway) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    public GetTenantOutput execute(final String tenantIdRaw) {
        final var tenantId = TenantId.from(tenantIdRaw);
        final var tenant = tenantGateway.findById(tenantId)
                .orElseThrow(() -> NotFoundException.with(Tenant.class, tenantId));

        return GetTenantOutput.of(
                tenant.getId().getValue(),
                tenant.getName(),
                tenant.getFantasyName(),
                tenant.getDocument().getValue(),
                tenant.getPhone1(),
                tenant.getPhone2(),
                tenant.getEmail().getValue(),
                tenant.getUrl().getValue(),
                tenant.getLogoUrl(),
                tenant.getStatus().name()
        );
    }
}

