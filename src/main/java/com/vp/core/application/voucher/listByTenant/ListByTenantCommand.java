package com.vp.core.application.voucher.listByTenant;

import com.vp.core.domain.pagination.SearchVoucherQuery;

public record ListByTenantCommand(String tenantId, SearchVoucherQuery searchQuery) {
}