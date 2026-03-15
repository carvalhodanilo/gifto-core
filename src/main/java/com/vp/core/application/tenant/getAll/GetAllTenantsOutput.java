package com.vp.core.application.tenant.getAll;

import com.vp.core.domain.tenant.Tenant;

import java.util.List;

public record GetAllTenantsOutput(
        List<TenantOutput> tenants
) {
    public static GetAllTenantsOutput of(
            List<Tenant> tenants
    ) {
        final var tenantOutputs = tenants.stream()
                .map(TenantOutput::of)
                .toList();

        return new GetAllTenantsOutput(tenantOutputs);
    }

    record TenantOutput(
            String id,
            String fantasyName
    ) {
        public static TenantOutput of(final Tenant tenant) {
            return new TenantOutput(
                    tenant.getId().getValue(),
                    tenant.getFantasyName()
            );
        }
    }
}