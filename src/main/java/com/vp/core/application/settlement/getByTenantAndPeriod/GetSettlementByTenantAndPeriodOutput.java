package com.vp.core.application.settlement.getByTenantAndPeriod;

import java.time.Instant;
import java.util.List;

public record GetSettlementByTenantAndPeriodOutput(
        String settlementBatchId,
        String periodKey,
        String status,
        Instant closedAt,
        List<SettlementEntryOutput> entries
) {
    public record SettlementEntryOutput(
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
    ) {}
}
