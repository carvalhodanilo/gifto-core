package com.vp.core.infrastructure.voucher.persistence.projection;

import java.time.Instant;
import java.util.UUID;

public interface VoucherIssuedProjection {

    UUID getId();

    UUID getCampaignId();

    String getCampaignName();

    String getDisplayCode();

    String getStatus();

    Instant getIssuedAt();

    Instant getExpiresAt();

    long getAmountCents();

    String getBuyerName();

    String getBuyerPhone();
}