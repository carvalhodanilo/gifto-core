package com.vp.core.application.voucher.issue;

import java.time.Instant;

public record IssueVoucherOutput(
        String voucherId,
        String token,
        String displayCode,
        Instant expiresAt
) {
    public static IssueVoucherOutput of(final String voucherId, final String token, final String displayCode, final Instant expiresAt) {
        return new IssueVoucherOutput(voucherId, token, displayCode, expiresAt);
    }
}