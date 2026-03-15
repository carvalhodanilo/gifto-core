package com.vp.core.application.voucher.listByTenant;

import java.time.Instant;

public record ListByTenantOutput(
        String voucherId,
        String campaignId,
        String campaignName,
        String displayCode,
        String status,
        Instant issuedAt,
        Instant expiresAt
) {
    public static ListByTenantOutput of(
            final String voucherId,
            final String campaignId,
            final String campaignName,
            final String displayCode,
            final String status,
            final Instant issuedAt,
            final Instant expiresAt
    ) {
        return new ListByTenantOutput(voucherId, campaignId, campaignName, displayCode, status, issuedAt, expiresAt);
    }
}