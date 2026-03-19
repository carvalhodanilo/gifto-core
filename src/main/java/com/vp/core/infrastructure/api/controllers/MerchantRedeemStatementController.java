package com.vp.core.infrastructure.api.controllers;

import com.vp.core.application.merchant.statement.ListMerchantRedeemsByPeriodCommand;
import com.vp.core.application.merchant.statement.ListMerchantRedeemsByPeriodOutput;
import com.vp.core.application.merchant.statement.ListMerchantRedeemsByPeriodUseCase;
import com.vp.core.application.security.AccessScopeService;
import com.vp.core.domain.pagination.SearchQuery;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/v1/merchants")
public class MerchantRedeemStatementController {

    private final ListMerchantRedeemsByPeriodUseCase useCase;
    private final AccessScopeService accessScopeService;

    public MerchantRedeemStatementController(
            final ListMerchantRedeemsByPeriodUseCase useCase,
            final AccessScopeService accessScopeService
    ) {
        this.useCase = useCase;
        this.accessScopeService = accessScopeService;
    }

    @GetMapping("/{merchantId}/redeems")
    // [tenant_admin, tenant_operator, merchant_admin, merchant_operator]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasAnyRole('tenant_admin', 'tenant_operator', 'merchant_admin', 'merchant_operator')")
    public ListMerchantRedeemsByPeriodOutput list(
            @PathVariable String merchantId,
            @RequestParam(required = false) OffsetDateTime from,
            @RequestParam(required = false) OffsetDateTime to,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        accessScopeService.ensureMerchantAccess(merchantId);

        final var now = OffsetDateTime.now();
        final var resolvedTo = (to == null ? now : to);
        final var resolvedFrom = (from == null ? resolvedTo.minusDays(7) : from);

        final ListMerchantRedeemsByPeriodCommand.SettlementFilter filter =
                "ALL".equalsIgnoreCase(status) ? null :
                        ListMerchantRedeemsByPeriodCommand.SettlementFilter.valueOf(status.toUpperCase());

        final var cmd = new ListMerchantRedeemsByPeriodCommand(
                merchantId,
                resolvedFrom.toInstant(),
                resolvedTo.toInstant(),
                filter,
                new SearchQuery(page, size, null, null, null)
        );
        return useCase.execute(cmd);
    }
}
