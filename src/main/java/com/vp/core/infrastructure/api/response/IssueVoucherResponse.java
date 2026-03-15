package com.vp.core.infrastructure.api.response;

import java.time.Instant;

public record IssueVoucherResponse(
        String voucherId,
        String publicToken,
        String displayCode,
        Instant expiresAt
) {
    public static IssueVoucherResponse of(
            final String voucherId,
            final String publicToken,
            final String displayCode,
            final Instant expiresAt
    ) {
        return new IssueVoucherResponse(voucherId, publicToken, displayCode, expiresAt);
    }
}