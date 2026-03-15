package com.vp.core.application.tenant.updateLocation;

public record UpdateTenantLocationCommand(
        String tenantId,
        String street,
        String number,
        String neighborhood,
        String complement,
        String city,
        String state,
        String country,
        String postalCode
) {
}