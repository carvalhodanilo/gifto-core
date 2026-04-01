package com.vp.core.infrastructure.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IssueVoucherRequest(
        @NotBlank String campaignId,
        @NotNull @Positive Long amountCents,
        @NotBlank String buyerName,
        @NotBlank String buyerPhone,
        String idempotencyKey //(via header)
) {
}