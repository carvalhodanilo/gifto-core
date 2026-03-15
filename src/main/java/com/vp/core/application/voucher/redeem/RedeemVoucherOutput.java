package com.vp.core.application.voucher.redeem;

import java.time.Instant;

public record RedeemVoucherOutput(
        String voucherId,
        String ledgerEntryId,
        long newBalanceCents,
        Instant createdAt
) {
    public static RedeemVoucherOutput of(
            final String voucherId,
            final String ledgerEntryId,
            final long newBalanceCents,
            final Instant createdAt
    ) {
        return new RedeemVoucherOutput(voucherId, ledgerEntryId, newBalanceCents, createdAt);
    }
}
