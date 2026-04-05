package com.vp.core.application.tenant.listPaged;

public record ListTenantsPagedOutput(
        String id,
        String name,
        String fantasyName,
        String document,
        String logoUrl,
        String status
) {
    public static ListTenantsPagedOutput of(
            final String id,
            final String name,
            final String fantasyName,
            final String document,
            final String logoUrl,
            final String status
    ) {
        return new ListTenantsPagedOutput(id, name, fantasyName, document, logoUrl, status);
    }
}

