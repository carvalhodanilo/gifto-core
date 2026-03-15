package com.vp.core.domain.merchant;

import com.vp.core.domain.Identifier;
import com.vp.core.domain.utils.IdUtils;

import java.util.Objects;

public final class MerchantId extends Identifier {

    private final String value;

    private MerchantId(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static MerchantId from(final String anId) {
        return new MerchantId(anId);
    }

    public static MerchantId newId() {
        return MerchantId.from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
