package com.vp.core.application.voucher.redeem;

public record RedeemVoucherCommand(
        String tenantId,
        String merchantId,
        long amountCents,
        String publicToken,
        String displayCode,
        String idempotencyKey
) {
}
