package com.vp.core.infrastructure.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IssueVoucherRequest(
        @NotBlank String tenantId,
        @NotBlank String campaignId,
        @NotNull @Positive Long amountCents,
        String idempotencyKey //(via header)
) {
}