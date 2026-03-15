package com.vp.core.domain.voucher;

import com.vp.core.domain.Identifier;
import com.vp.core.domain.utils.IdUtils;

import java.util.Objects;

public final class VoucherId extends Identifier {

    private final String value;

    private VoucherId(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static VoucherId from(final String anId) {
        return new VoucherId(anId);
    }

    public static VoucherId newId() {
        return VoucherId.from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
