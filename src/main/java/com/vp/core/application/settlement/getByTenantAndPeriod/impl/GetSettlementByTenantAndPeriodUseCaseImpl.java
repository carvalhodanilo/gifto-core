package com.vp.core.application.settlement.getByTenantAndPeriod.impl;

import com.vp.core.application.settlement.getByTenantAndPeriod.GetSettlementByTenantAndPeriodCommand;
import com.vp.core.application.settlement.getByTenantAndPeriod.GetSettlementByTenantAndPeriodOutput;
import com.vp.core.application.settlement.getByTenantAndPeriod.GetSettlementByTenantAndPeriodUseCase;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.gateway.SettlementBatchGateway;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.settlement.PeriodKey;
import com.vp.core.domain.settlement.SettlementBatch;
import com.vp.core.domain.settlement.SettlementEntry;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.validation.DomainError;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GetSettlementByTenantAndPeriodUseCaseImpl extends GetSettlementByTenantAndPeriodUseCase {

    private final SettlementBatchGateway settlementGateway;
    private final MerchantGateway merchantGateway;

    public GetSettlementByTenantAndPeriodUseCaseImpl(
            final SettlementBatchGateway settlementGateway,
            final MerchantGateway merchantGateway
    ) {
        this.settlementGateway = settlementGateway;
        this.merchantGateway = merchantGateway;
    }

    @Override
    public GetSettlementByTenantAndPeriodOutput execute(final GetSettlementByTenantAndPeriodCommand command) {
        final var periodKey = PeriodKey.from(command.periodKey());
        final var tenantId = command.tenantId();

        final var batch = settlementGateway
                .findByTenantIdAndPeriodKey(tenantId, periodKey.getValue())
                .orElseThrow(() -> NotFoundException.with(
                        new DomainError("Settlement batch not found for tenant and period " + periodKey.getValue())
                ));

        return toOutput(batch, tenantId);
    }

    private GetSettlementByTenantAndPeriodOutput toOutput(final SettlementBatch batch, final TenantId tenantId) {
        final var merchantNames = resolveMerchantNames(batch, tenantId);
        final var entries = batch.entries().stream()
                .map(e -> toEntryOutput(e, merchantNames))
                .toList();

        return new GetSettlementByTenantAndPeriodOutput(
                batch.getId().getValue(),
                batch.periodKey(),
                batch.status().name(),
                batch.closedAt(),
                entries
        );
    }

    private Map<String, String> resolveMerchantNames(final SettlementBatch batch, final TenantId tenantId) {
        final var map = new HashMap<String, String>();
        for (final SettlementEntry e : batch.entries()) {
            final var merchantId = e.merchantId();
            if (!map.containsKey(merchantId.getValue())) {
                final var name = merchantGateway
                        .findByIdAndTenantId(merchantId, tenantId)
                        .map(m -> m.name())
                        .orElse(null);
                map.put(merchantId.getValue(), name);
            }
        }
        return map;
    }

    private static GetSettlementByTenantAndPeriodOutput.SettlementEntryOutput toEntryOutput(
            final SettlementEntry e,
            final Map<String, String> merchantNames
    ) {
        final var merchantName = merchantNames.get(e.merchantId().getValue());
        return new GetSettlementByTenantAndPeriodOutput.SettlementEntryOutput(
                e.getId().getValue(),
                e.merchantId().getValue(),
                merchantName,
                e.grossCents(),
                e.reversalsCents(),
                e.feesCents(),
                e.netCents(),
                e.status().name(),
                e.paidAt(),
                e.paymentRef()
        );
    }
}
