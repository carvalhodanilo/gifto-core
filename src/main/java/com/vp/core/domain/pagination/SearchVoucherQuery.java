package com.vp.core.domain.pagination;

public record SearchVoucherQuery(
        int page,
        int perPage,
        boolean active,
        String campaignName,
        String displayCode,
        String buyerName,
        String buyerPhone
) {
}
