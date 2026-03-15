package com.vp.core.domain.validation;

public abstract class Validator {

    private final ValidationHandler handler;

    protected Validator(final ValidationHandler aHandler) {
        this.handler = aHandler;
    }

    public abstract void validate();

    protected ValidationHandler validationHandler() {
        return this.handler;
    }

    protected boolean appendErrorIfIsNullOrEmpty(Object fieldToValidate, String fieldName) {
        if (fieldToValidate == null) {
            handler.append(new DomainError("'" + fieldName + "' must not be null"));
            return true;
        }

        if (fieldToValidate instanceof String && ((String)fieldToValidate).isBlank()) {
            handler.append(new DomainError("'" + fieldName + "' must not be empty"));
            return true;
        }
        return false;
    }

    protected boolean appendErrorIfIsIncorrectLength(Object fieldToValidate, String fieldName, int min, int max) {
        if (fieldToValidate instanceof String validate) {
            final int length = validate.trim().length();
            if (length > max || length < min) {
                handler.append(new DomainError("'" + fieldName + "' length must be between " + min + " and " + max));
                return true;
            }
        }
        return false;
    }

    protected boolean appendErrorIfIsNegativeValue(Object fieldToValidate, String fieldName) {
        if (fieldToValidate instanceof Number validate) {
            if (validate.doubleValue() < 0.0) {
                this.validationHandler().append(new DomainError("'" + fieldName + "' must not be less than 0"));
                return true;
            }
        }
        return false;
    }
}
