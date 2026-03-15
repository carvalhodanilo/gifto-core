package com.vp.core.application.voucher.reversal;

public record ReverseRedeemOutput(
        String voucherId,
        String reversalLedgerEntryId,
        long newBalanceCents
) {
    public static ReverseRedeemOutput of(
            final String voucherId,
            final String reversalLedgerEntryId,
            final long newBalanceCents
    ) {
        return new ReverseRedeemOutput(voucherId, reversalLedgerEntryId, newBalanceCents);
    }
}
