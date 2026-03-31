package com.vp.core.application.tenant.listPaged;

public record ListTenantsPagedOutput(
        String id,
        String name,
        String fantasyName,
        String document,
        String status
) {
    public static ListTenantsPagedOutput of(
            final String id,
            final String name,
            final String fantasyName,
            final String document,
            final String status
    ) {
        return new ListTenantsPagedOutput(id, name, fantasyName, document, status);
    }
}

