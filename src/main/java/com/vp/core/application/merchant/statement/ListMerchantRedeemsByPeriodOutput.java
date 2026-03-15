package com.vp.core.application.merchant.statement;

import com.vp.core.domain.pagination.Pagination;

import java.time.Instant;

public record ListMerchantRedeemsByPeriodOutput(
        Period period,
        Summary summary,
        Pagination<RedeemItem> pagination
) {
    public record Period(Instant from, Instant to) {}

    public record Summary(
            long grossRedeemsCents,
            long reversalsCents,
            long netSubtotalCents
    ) {}

    public record RedeemItem(
            String ledgerEntryId,
            String voucherId,
            String displayCode,
            long amountCents,
            Instant createdAt,
            Settlement settlement
    ) {}

    public record Settlement(
            SettlementStatus status,
            String settlementEntryId,
            String settlementBatchId,
            Instant paidAt
    ) {
        public enum SettlementStatus { PENDING, PAID }
    }
}