package com.vp.core.domain.network;

import com.vp.core.domain.Identifier;
import com.vp.core.domain.utils.IdUtils;

import java.util.Objects;

public final class NetworkId extends Identifier {

    private final String value;

    private NetworkId(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static NetworkId from(final String anId) {
        return new NetworkId(anId);
    }

    public static NetworkId newId() {
        return NetworkId.from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
