package com.vp.core.application.voucher.listByTenant.impl;

import com.vp.core.application.VoucherTokenService;
import com.vp.core.application.voucher.getByToken.GetByTokenCommand;
import com.vp.core.application.voucher.getByToken.GetByTokenOutput;
import com.vp.core.application.voucher.getByToken.GetByTokenUseCase;
import com.vp.core.application.voucher.listByTenant.ListByTenantCommand;
import com.vp.core.application.voucher.listByTenant.ListByTenantOutput;
import com.vp.core.application.voucher.listByTenant.ListByTenantUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.VoucherGateway;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.validation.DomainError;
import org.springframework.stereotype.Service;

@Service
public class ListByTenantUseCaseImpl extends ListByTenantUseCase {

    private final VoucherGateway voucherGateway;
    private final VoucherTokenService tokenService;

    public ListByTenantUseCaseImpl(
            final VoucherGateway voucherGateway,
            final VoucherTokenService tokenService
    ) {
        this.voucherGateway = voucherGateway;
        this.tokenService = tokenService;
    }

    @Override
    public Pagination<ListByTenantOutput> execute(ListByTenantCommand command) {
        return voucherGateway.findAllByTenant(command.tenantId(), command.searchQuery())
                .map(voucher -> ListByTenantOutput.of(
                        voucher.getId().toString(),
                        voucher.getCampaignId().toString(),
                        voucher.getCampaignName(),
                        voucher.getStatus(),
                        voucher.getAmountCents(),
                        voucher.getIssuedAt(),
                        voucher.getExpiresAt(),
                        voucher.getBuyerName(),
                        voucher.getBuyerPhone()
                ));
    }
}