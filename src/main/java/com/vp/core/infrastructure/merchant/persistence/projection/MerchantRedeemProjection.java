package com.vp.core.infrastructure.merchant.persistence.projection;

import java.time.Instant;
import java.util.UUID;

public interface MerchantRedeemProjection {
    UUID getLedgerEntryId();
    UUID getVoucherId();
    String getDisplayCode();
    long getAmountCents();
    Instant getCreatedAt();

    UUID getSettlementEntryId();
    UUID getSettlementBatchId();
    String getSettlementStatus();
    Instant getPaidAt();
}