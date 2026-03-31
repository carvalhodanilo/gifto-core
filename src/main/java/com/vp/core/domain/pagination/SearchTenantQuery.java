package com.vp.core.domain.pagination;

public record SearchTenantQuery(
        int page,
        int perPage,
        String name,
        String document
) {
}

