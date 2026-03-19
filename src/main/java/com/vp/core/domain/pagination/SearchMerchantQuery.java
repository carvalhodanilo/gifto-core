package com.vp.core.domain.pagination;

public record SearchMerchantQuery(
        int page,
        int perPage,
        String terms,
        String status
) {
}
