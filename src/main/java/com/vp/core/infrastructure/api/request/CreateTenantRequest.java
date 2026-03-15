package com.vp.core.infrastructure.api.request;

public record CreateTenantRequest(
        String name,
        String fantasyName,
        String document,
        String phone1,
        String email,
        String url
) {}
