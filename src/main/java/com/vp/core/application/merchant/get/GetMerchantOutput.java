package com.vp.core.application.merchant.get;

import java.time.Instant;
import java.util.List;

public record GetMerchantOutput(
        String merchantId,
        String name,
        String fantasyName,
        String document,
        String email,
        String phone1,
        String phone2,
        String url,
        String status,
        LocationOutput location,
        List<String> activeNetworkIds,
        Instant createdAt,
        Instant updatedAt
) {

    public record LocationOutput(
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

    public static GetMerchantOutput of(
            final String merchantId,
            final String name,
            final String fantasyName,
            final String document,
            final String email,
            final String phone1,
            final String phone2,
            final String url,
            final String status,
            final LocationOutput location,
            final List<String> activeNetworkIds,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new GetMerchantOutput(
                merchantId,
                name,
                fantasyName,
                document,
                email,
                phone1,
                phone2,
                url,
                status,
                location,
                activeNetworkIds,
                createdAt,
                updatedAt
        );
    }
}
