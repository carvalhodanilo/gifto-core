package com.vp.core.application.tenant.listPaged.impl;

import com.vp.core.application.tenant.listPaged.ListTenantsPagedCommand;
import com.vp.core.application.tenant.listPaged.ListTenantsPagedOutput;
import com.vp.core.application.tenant.listPaged.ListTenantsPagedUseCase;
import com.vp.core.domain.gateway.TenantGateway;
import org.springframework.stereotype.Service;

@Service
public class ListTenantsPagedUseCaseImpl extends ListTenantsPagedUseCase {

    private final TenantGateway tenantGateway;

    public ListTenantsPagedUseCaseImpl(final TenantGateway tenantGateway) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    public com.vp.core.domain.pagination.Pagination<ListTenantsPagedOutput> execute(final ListTenantsPagedCommand command) {
        return tenantGateway.findAllPaged(command.searchQuery())
                .map(t -> ListTenantsPagedOutput.of(
                        t.getId(),
                        t.getName(),
                        t.getFantasyName(),
                        t.getDocumentValue(),
                        t.getLogoUrl(),
                        t.getStatus()
                ));
    }
}

