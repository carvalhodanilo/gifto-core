package com.vp.core.application.voucher.getLegderEntriesByMerchant;

import com.vp.core.domain.pagination.SearchQuery;

public record ListLedgerEntriesByMerchantCommand(String tenantId, String merchantId, SearchQuery searchQuery) {
}