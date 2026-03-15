package com.vp.core.domain.user;

import com.vp.core.domain.Identifier;
import com.vp.core.domain.utils.IdUtils;

import java.util.Objects;

public final class UserId extends Identifier {

    private final String value;

    private UserId(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static UserId from(final String anId) {
        return new UserId(anId);
    }

    public static UserId newId() {
        return UserId.from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
