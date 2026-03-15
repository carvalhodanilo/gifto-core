package com.vp.core.infrastructure.api.request;

/**
 * Request para marcar uma entry de settlement como paga.
 */
public record MarkSettlementEntryAsPaidRequest(
        String paymentRef
) {
}
