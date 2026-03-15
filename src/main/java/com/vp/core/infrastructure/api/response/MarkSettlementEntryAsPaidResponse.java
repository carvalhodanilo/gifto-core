package com.vp.core.infrastructure.api.response;

import com.vp.core.application.settlement.markAsPaid.MarkSettlementEntryAsPaidOutput;

public record MarkSettlementEntryAsPaidResponse(
        String settlementBatchId,
        String entryId
) {
    public static MarkSettlementEntryAsPaidResponse from(final MarkSettlementEntryAsPaidOutput output) {
        return new MarkSettlementEntryAsPaidResponse(
                output.batchId().getValue(),
                output.entryId().getValue()
        );
    }
}
