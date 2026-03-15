package com.vp.core.domain.campaign;

import com.vp.core.domain.Identifier;
import com.vp.core.domain.utils.IdUtils;

import java.util.Objects;

public final class CampaignId extends Identifier {

    private final String value;

    private CampaignId(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static CampaignId from(final String anId) {
        return new CampaignId(anId);
    }

    public static CampaignId newId() {
        return CampaignId.from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
