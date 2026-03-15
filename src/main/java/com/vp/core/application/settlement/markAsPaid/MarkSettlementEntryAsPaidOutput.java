package com.vp.core.application.settlement.markAsPaid;

import com.vp.core.domain.settlement.SettlementBatchId;
import com.vp.core.domain.settlement.SettlementEntryId;

public record MarkSettlementEntryAsPaidOutput(
        SettlementBatchId batchId,
        SettlementEntryId entryId
) {
    public static MarkSettlementEntryAsPaidOutput from(
            SettlementBatchId batchId,
            SettlementEntryId entryId
    ) {
        return new MarkSettlementEntryAsPaidOutput(batchId, entryId);
    }
}
