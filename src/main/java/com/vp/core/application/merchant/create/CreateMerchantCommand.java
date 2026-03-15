package com.vp.core.application.merchant.create;

import com.vp.core.domain.valueObjects.URL;

import java.util.Set;

public record CreateMerchantCommand(
        String tenantId,
        String name,
        String fantasyName,
        String document,
        String phone1,
        String phone2,
        String email,
        URL url
) {

    public static CreateMerchantCommand with(
            final String tenantId,
            final String name,
            final String fantasyName,
            final String document,
            final String phone1,
            final String phone2,
            final String email,
            final String url
    ) {
        return new CreateMerchantCommand(
                tenantId,
                name,
                fantasyName,
                document,
                phone1,
                phone2,
                email,
                URL.with(url)
        );
    }
}
