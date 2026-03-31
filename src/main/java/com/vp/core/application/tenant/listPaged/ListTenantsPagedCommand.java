package com.vp.core.application.tenant.listPaged;

import com.vp.core.domain.pagination.SearchTenantQuery;

public record ListTenantsPagedCommand(SearchTenantQuery searchQuery) {
}

