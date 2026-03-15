package com.vp.core.application.voucher.getByDisplayCode;

import java.time.Instant;

public record GetByDisplayCodeOutput(
        String voucherId,
        String campaignName,
        String displayCode,
        String status,
        Instant expiresAt,
        long balanceCents
) {
    public static GetByDisplayCodeOutput of(
            final String voucherId,
            final String campaignName,
            final String displayCode,
            final String status,
            final Instant expiresAt,
            final long balanceCents
    ) {
        return new GetByDisplayCodeOutput(
                voucherId,
                campaignName,
                status,
                displayCode,
                expiresAt,
                balanceCents
        );
    }
}