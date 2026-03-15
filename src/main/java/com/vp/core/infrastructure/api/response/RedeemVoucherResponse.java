package com.vp.core.infrastructure.api.response;

public record RedeemVoucherResponse(
        String voucherId,
        long newBalanceCents
) {
    public static RedeemVoucherResponse of(final String voucherId, final long newBalanceCents) {
        return new RedeemVoucherResponse(voucherId, newBalanceCents);
    }
}