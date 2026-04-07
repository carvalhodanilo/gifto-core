package com.vp.core.infrastructure.merchant.persistence.projection;

import java.util.UUID;

public interface PublicCampaignStoreProjection {
    UUID getId();

    String getFantasyName();

    String getName();

    String getLandingLogoUrl();

    String getCity();
}
