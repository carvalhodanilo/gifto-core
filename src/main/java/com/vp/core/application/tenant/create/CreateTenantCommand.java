package com.vp.core.application.tenant.create;

import com.vp.core.domain.valueObjects.URL;

import java.util.Set;

public record CreateTenantCommand(
        String legalName,
        String fantasyName,
        String document,
        String phone1,
        String contactName,
        String contactEmail,
        URL url
) {

    public static CreateTenantCommand with(
            final String legalName,
            final String fantasyName,
            final String document,
            final String phone1,
            final String contactEmail,
            final String url
    ) {
        return new CreateTenantCommand(
                legalName,
                fantasyName,
                document,
                phone1,
                contactEmail,
                contactEmail,
                URL.with(url)
        );
    }
}
