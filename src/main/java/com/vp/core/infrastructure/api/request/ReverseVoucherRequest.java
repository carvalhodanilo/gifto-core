package com.vp.core.infrastructure.api.request;

import jakarta.validation.constraints.NotBlank;

public record ReverseVoucherRequest(
        @NotBlank String tenantId,
        @NotBlank String merchantId,
        @NotBlank String refLedgerEntryId,
        @NotBlank String displayCode,
        String publicToken,
        String idempotencyKey
) {}