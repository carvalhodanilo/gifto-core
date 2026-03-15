package com.vp.core.infrastructure.api.response;

import com.vp.core.application.merchant.create.CreateMerchantOutput;

public record CreateMerchantResponse(
        String merchantId
) {
    public static CreateMerchantResponse from(final CreateMerchantOutput output) {
        return new CreateMerchantResponse(output.merchantId());
    }
}