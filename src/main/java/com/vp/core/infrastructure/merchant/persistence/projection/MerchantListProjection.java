package com.vp.core.infrastructure.merchant.persistence.projection;

import java.util.UUID;

public interface MerchantListProjection {

    UUID getId();

    String getFantasyName();

    String getStatus();
}
