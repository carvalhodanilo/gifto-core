package com.vp.core.application.voucher.getLegderEntriesByMerchant;

public record ListLedgerEntriesByMerchantOutput(
        String ledgerEntryId,
        String type,
        long amountCents,
        String createdAt,
        String displayCode
) {
    public static ListLedgerEntriesByMerchantOutput of(
            String ledgerEntryId,
            String type,
            long amountCents,
            String createdAt,
            String displayCode
    ) {
        return new ListLedgerEntriesByMerchantOutput(ledgerEntryId, type, amountCents, createdAt, displayCode);
    }
}