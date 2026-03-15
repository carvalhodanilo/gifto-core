package com.vp.core.infrastructure.api.response;

import com.vp.core.application.settlement.getByTenantAndPeriod.GetSettlementByTenantAndPeriodOutput;

import java.time.Instant;
import java.util.List;

public record GetSettlementByTenantAndPeriodResponse(
        String settlementBatchId,
        String periodKey,
        String status,
        Instant closedAt,
        List<SettlementEntryResponse> entries
) {
    public record SettlementEntryResponse(
            String entryId,
            String merchantId,
            String merchantName,
            long grossCents,
            long reversalsCents,
            long feesCents,
            long netCents,
            String status,
            Instant paidAt,
            String paymentRef
    ) {
    }

    public static GetSettlementByTenantAndPeriodResponse from(final GetSettlementByTenantAndPeriodOutput output) {
        final var entries = output.entries().stream()
                .map(e -> new SettlementEntryResponse(
                        e.entryId(),
                        e.merchantId(),
                        e.merchantName(),
                        e.grossCents(),
                        e.reversalsCents(),
                        e.feesCents(),
                        e.netCents(),
                        e.status(),
                        e.paidAt(),
                        e.paymentRef()
                ))
                .toList();
        return new GetSettlementByTenantAndPeriodResponse(
                output.settlementBatchId(),
                output.periodKey(),
                output.status(),
                output.closedAt(),
                entries
        );
    }
}
