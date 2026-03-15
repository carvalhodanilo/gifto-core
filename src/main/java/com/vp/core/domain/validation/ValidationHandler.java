package com.vp.core.domain.validation;

import java.util.List;

public interface ValidationHandler {

    ValidationHandler append(DomainError anDomainError);

    ValidationHandler append(ValidationHandler anHandler);

    <T> T validate(Validation<T> aValidation);

    List<DomainError> getErrors();

    default boolean hasError() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    interface Validation<T> {
        T validate();
    }
}
