package com.vp.core.domain.valueObjects;

import com.vp.core.domain.ValueObject;
import com.vp.core.domain.validation.ValidationHandler;

import java.util.Objects;

public class PixKey extends ValueObject {

    private final PixKeyType type;
    private final String value;

    private PixKey(final PixKeyType type, final String value) {
        this.type = type;
        this.value = value;
    }

    public static PixKey of(final PixKeyType type, final String value) {
        return new PixKey(type, value);
    }

    public void validate(final ValidationHandler handler) {
    }

    public PixKeyType getType() { return type; }
    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PixKey pixKey)) return false;
        return type == pixKey.type && Objects.equals(value, pixKey.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    public enum PixKeyType {
        CPF,
        CNPJ,
        EMAIL,
        PHONE,
        RANDOM
    }
}
