package com.vp.core.application.tenant.get;

public record GetTenantOutput(
        String id,
        String name,
        String fantasyName,
        String document,
        String phone1,
        String phone2,
        String email,
        String url,
        String status
) {
    public static GetTenantOutput of(
            final String id,
            final String name,
            final String fantasyName,
            final String document,
            final String phone1,
            final String phone2,
            final String email,
            final String url,
            final String status
    ) {
        return new GetTenantOutput(id, name, fantasyName, document, phone1, phone2, email, url, status);
    }
}

