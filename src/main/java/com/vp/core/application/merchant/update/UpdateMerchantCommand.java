package com.vp.core.application.merchant.update;

import com.vp.core.domain.valueObjects.URL;

public record UpdateMerchantCommand(
        String tenantId,
        String merchantId,
        String name,
        String fantasyName,
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
}