package com.vp.core.application.merchant.listByTenant.impl;

import com.vp.core.application.merchant.listByTenant.ListMerchantsByTenantCommand;
import com.vp.core.application.merchant.listByTenant.ListMerchantsByTenantOutput;
import com.vp.core.application.merchant.listByTenant.ListMerchantsByTenantUseCase;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.tenant.TenantId;
import org.springframework.stereotype.Service;

@Service
public class ListMerchantsByTenantUseCaseImpl extends ListMerchantsByTenantUseCase {

    private final MerchantGateway merchantGateway;

    public ListMerchantsByTenantUseCaseImpl(final MerchantGateway merchantGateway) {
        this.merchantGateway = merchantGateway;
    }

    @Override
    public Pagination<ListMerchantsByTenantOutput> execute(ListMerchantsByTenantCommand command) {
        var tenantId = TenantId.from(command.tenantId());
        return merchantGateway.findAllByTenantId(tenantId, command.searchQuery())
                .map(p -> ListMerchantsByTenantOutput.of(
                        p.getId().toString(),
                        p.getFantasyName(),
                        p.getLandingLogoUrl(),
                        p.getStatus()
                ));
    }
}
