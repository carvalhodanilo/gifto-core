package com.vp.core.domain.valueObjects;

import com.vp.core.domain.ValueObject;
import com.vp.core.domain.validation.ValidationHandler;

import java.util.Objects;

public class Document extends ValueObject {

    private final String value;

    private Document(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }

    public static Document with(String value) {
        return new Document(value);
    }

    public void validate(final ValidationHandler handler) {
        new DocumentValidator(this, handler).validate();
    }
}
