package com.vp.core.domain.settlement;

import com.vp.core.domain.Identifier;
import com.vp.core.domain.utils.IdUtils;

import java.util.Objects;

public final class SettlementBatchId extends Identifier {

    private final String value;

    private SettlementBatchId(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static SettlementBatchId from(final String anId) {
        return new SettlementBatchId(anId);
    }

    public static SettlementBatchId newId() {
        return SettlementBatchId.from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
