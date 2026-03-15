package com.vp.core.domain.settlement;

import com.vp.core.domain.Identifier;
import com.vp.core.domain.utils.IdUtils;

import java.util.Objects;

public final class SettlementEntryId extends Identifier {

    private final String value;

    private SettlementEntryId(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static SettlementEntryId from(final String anId) {
        return new SettlementEntryId(anId);
    }

    public static SettlementEntryId newId() {
        return SettlementEntryId.from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
