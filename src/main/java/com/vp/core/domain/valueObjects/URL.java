package com.vp.core.domain.valueObjects;

import com.vp.core.domain.ValueObject;
import com.vp.core.domain.utils.IdUtils;
import com.vp.core.domain.validation.ValidationHandler;

import java.util.Objects;

public class URL extends ValueObject {

    private final String value;
    private final URLType type;

    private URL(String value, URLType type) {
        this.value = Objects.requireNonNull(value);
        this.type = Objects.requireNonNull(type);
    }

    public String getValue() {
        return value;
    }

    public URLType getType() {
        return type;
    }

    public static URL with(String value){
        return new URL(value, URLType.SITE);
    }

    public void validate(final ValidationHandler handler) {
        new URLValidator(this, handler).validate();
    }
}
