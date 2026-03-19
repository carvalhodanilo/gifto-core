package com.vp.core.infrastructure.api.request;

public record UpdateMerchantRequest(
        String name,
        String fantasyName,
        String phone1,
        String phone2,
        String email,
        String url,
        LocationRequest location
) {
}
