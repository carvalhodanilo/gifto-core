package com.vp.core.domain.voucher;

import com.vp.core.domain.Identifier;
import com.vp.core.domain.utils.IdUtils;

import java.util.Objects;

public final class LedgerEntryId extends Identifier {

    private final String value;

    private LedgerEntryId(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static LedgerEntryId from(final String anId) {
        return new LedgerEntryId(anId);
    }

    public static LedgerEntryId newId() {
        return LedgerEntryId.from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
