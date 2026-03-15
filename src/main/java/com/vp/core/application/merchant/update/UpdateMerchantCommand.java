package com.vp.core.application.merchant.update;

import com.vp.core.domain.valueObjects.URL;

import java.util.Set;

public record UpdateMerchantCommand(
        String merchantId,
        String name,
        String fantasyName,
        String phone1,
        String phone2,
        String email,
        URL url
) {
}