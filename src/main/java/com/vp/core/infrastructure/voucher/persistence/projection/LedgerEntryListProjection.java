package com.vp.core.infrastructure.voucher.persistence.projection;

import java.time.Instant;
import java.util.UUID;

public interface LedgerEntryListProjection {
    UUID getLedgerEntryId();
    String getType();
    Long getAmountCents();
    Instant getCreatedAt();
    String getDisplayCode();
}