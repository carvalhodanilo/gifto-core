package com.vp.core.application.voucher.listByTenant;

import java.time.Instant;

public record ListByTenantOutput(
        String voucherId,
        String campaignId,
        String campaignName,
        String status,
        long amountCents,
        Instant issuedAt,
        Instant expiresAt,
        String buyerName,
        String buyerPhone
) {
    public static ListByTenantOutput of(
            final String voucherId,
            final String campaignId,
            final String campaignName,
            final String status,
            final long amountCents,
            final Instant issuedAt,
            final Instant expiresAt,
            final String buyerName,
            final String buyerPhone
    ) {
        return new ListByTenantOutput(
                voucherId,
                campaignId,
                campaignName,
                status,
                amountCents,
                issuedAt,
                expiresAt,
                buyerName,
                buyerPhone
        );
    }
}