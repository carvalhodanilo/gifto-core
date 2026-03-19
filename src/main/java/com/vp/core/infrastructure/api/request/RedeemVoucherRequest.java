package com.vp.core.infrastructure.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RedeemVoucherRequest(
        @NotNull @Positive Long amountCents,
        String publicToken,
        @NotNull String displayCode,
        String idempotencyKey
) {}