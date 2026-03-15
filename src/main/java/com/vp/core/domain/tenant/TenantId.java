package com.vp.core.domain.tenant;

import com.vp.core.domain.Identifier;
import com.vp.core.domain.utils.IdUtils;

import java.util.Objects;

public final class TenantId extends Identifier {

    private final String value;

    private TenantId(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static TenantId from(final String anId) {
        return new TenantId(anId);
    }

    public static TenantId newId() {
        return TenantId.from(IdUtils.uuid());
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
