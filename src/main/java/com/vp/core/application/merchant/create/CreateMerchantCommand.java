package com.vp.core.application.merchant.create;

import com.vp.core.domain.valueObjects.URL;

public record CreateMerchantCommand(
        String tenantId,
        String name,
        String fantasyName,
        String document,
        String phone1,
        String phone2,
        String email,
        URL url,
        String street,
        String number,
        String neighborhood,
        String complement,
        String city,
        String state,
        String country,
        String postalCode
) {

    public static CreateMerchantCommand with(
            final String tenantId,
            final String name,
            final String fantasyName,
            final String document,
            final String phone1,
            final String phone2,
            final String email,
            final String url,
            final String street,
            final String number,
            final String neighborhood,
            final String complement,
            final String city,
            final String state,
            final String country,
            final String postalCode
    ) {
        return new CreateMerchantCommand(
                tenantId,
                name,
                fantasyName,
                document,
                phone1,
                phone2,
                email,
                URL.with(url),
                street,
                number,
                neighborhood,
                complement,
                city,
                state,
                country,
                postalCode
        );
    }
}
