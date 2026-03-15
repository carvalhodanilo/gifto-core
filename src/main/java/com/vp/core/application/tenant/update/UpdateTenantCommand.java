package com.vp.core.application.tenant.update;

import com.vp.core.domain.valueObjects.URL;

import java.util.Set;

public record UpdateTenantCommand(
        String id,
        String legalName,
        String fantasyName,
        String phone1,
        String phone2,
        String contactEmail,
        URL url
) {
}