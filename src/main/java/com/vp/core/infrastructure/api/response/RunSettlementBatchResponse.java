package com.vp.core.infrastructure.api.response;

import com.vp.core.application.settlement.runBatch.RunSettlementBatchOutput;

public record RunSettlementBatchResponse(
        String settlementBatchId
) {
    public static RunSettlementBatchResponse from(final RunSettlementBatchOutput output) {
        return new RunSettlementBatchResponse(output.settlementBatchId());
    }
}
