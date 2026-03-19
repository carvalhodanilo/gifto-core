package com.vp.core.application.merchant.listByTenant;

import com.vp.core.domain.pagination.SearchMerchantQuery;

public record ListMerchantsByTenantCommand(String tenantId, SearchMerchantQuery searchQuery) {
}
