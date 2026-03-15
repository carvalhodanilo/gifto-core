package com.vp.core.application.voucher.reversal;

public record ReverseRedeemCommand(
        String tenantId,
        String merchantId,
        String refLedgerEntryId,
        String token,
        String displayCode,
        String idempotencyKey
) {
}
