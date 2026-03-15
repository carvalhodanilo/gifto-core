package com.vp.core.application.settlement.markAsPaid;

import com.vp.core.domain.settlement.SettlementBatchId;
import com.vp.core.domain.settlement.SettlementEntryId;

public record MarkSettlementEntryAsPaidCommand(
        SettlementBatchId batchId,
        SettlementEntryId entryId,
        String paymentRef
) {}
