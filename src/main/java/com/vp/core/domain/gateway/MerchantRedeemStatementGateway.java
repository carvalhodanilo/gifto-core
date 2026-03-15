package com.vp.core.domain.gateway;

import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;

import java.time.Instant;

public interface MerchantRedeemStatementGateway {

    enum SettlementFilter {PENDING, PAID} // null => ALL no app layer

    Pagination<RedeemRow> findRedeems(
            MerchantId merchantId,
            Instant from,
            Instant to,
            SettlementFilter filter,
            SearchQuery query
    );

    Totals totals(
            MerchantId merchantId,
            Instant from,
            Instant to,
            SettlementFilter filter
    );

    record RedeemRow(
            String ledgerEntryId,
            String voucherId,
            String displayCode,
            long amountCents,
            Instant createdAt,
            String settlementEntryId,
            String settlementBatchId,
            String settlementStatus, // "PAID" | "OPEN" | null
            Instant paidAt
    ) {
    }

    record Totals(
            long getGrossRedeemsCents,
            long getReversalsCents,
            long getNetSubtotalCents
    ) {
    }
}