package com.vp.core.infrastructure.api.request;

public record LocationRequest(
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
