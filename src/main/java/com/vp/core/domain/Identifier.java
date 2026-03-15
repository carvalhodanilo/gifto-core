package com.vp.core.domain;

public abstract class Identifier extends ValueObject {
    public abstract String getValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public String toString() {
        return getValue();
    }
}
