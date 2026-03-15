package com.vp.core.application.merchant.updateLocation;

public record UpdateMerchantLocationCommand(
        String tenantId,
        String merchantId,
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