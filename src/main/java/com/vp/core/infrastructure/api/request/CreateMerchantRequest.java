package com.vp.core.infrastructure.api.request;

public record CreateMerchantRequest(
        String name,
        String fantasyName,
        String document,
        String phone1,
        String phone2,
        String email,
        String url
) {
}
