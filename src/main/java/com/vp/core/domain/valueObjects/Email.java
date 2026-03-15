package com.vp.core.domain.valueObjects;

import com.vp.core.domain.ValueObject;
import com.vp.core.domain.validation.ValidationHandler;

import java.util.Objects;

public class Email extends ValueObject {

    private final String value;

    private Email(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public static Email with(String value){
        return new Email(value);
    }

    public void validate(final ValidationHandler handler) {
        new EmailValidator(this, handler).validate();
    }

    public String getValue() {
        return value;
    }
}
