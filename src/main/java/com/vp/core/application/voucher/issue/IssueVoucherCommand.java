package com.vp.core.application.voucher.issue;

public record IssueVoucherCommand(
        String tenantId,
        String campaignId,
        long amountCents,
        String idempotencyKey
) {
}