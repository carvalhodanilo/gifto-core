package com.vp.core.application.merchant.statement;

import com.vp.core.domain.pagination.SearchQuery;

import java.time.Instant;

public record ListMerchantRedeemsByPeriodCommand(
        String merchantId,
        Instant from,
        Instant to,
        SettlementFilter settlementStatus, // null => ALL
        SearchQuery query
) {
    public enum SettlementFilter { PENDING, PAID }
}