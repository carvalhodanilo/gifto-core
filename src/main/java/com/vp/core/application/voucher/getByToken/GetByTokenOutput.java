package com.vp.core.application.voucher.getByToken;

import java.time.Instant;

public record GetByTokenOutput(
        String voucherId,
        String campaignId,
        String status,
        Instant expiresAt,
        long balanceCents
) {
    public static GetByTokenOutput of(
            final String voucherId,
            final String campaignId,
            final String status,
            final Instant expiresAt,
            final long balanceCents
    ) {
        return new GetByTokenOutput(voucherId, campaignId, status, expiresAt, balanceCents);
    }
}