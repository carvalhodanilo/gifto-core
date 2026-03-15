package com.vp.core.application.settlement.runBatch;

public record RunSettlementBatchOutput(String settlementBatchId) {
    public static RunSettlementBatchOutput of(final String settlementBatchId) {
        return new RunSettlementBatchOutput(settlementBatchId);
    }
}
