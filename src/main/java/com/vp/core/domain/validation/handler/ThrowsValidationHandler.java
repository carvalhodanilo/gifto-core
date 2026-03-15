package com.vp.core.domain.validation.handler;


import com.vp.core.domain.exceptions.DomainException;
import com.vp.core.domain.validation.DomainError;
import com.vp.core.domain.validation.ValidationHandler;

import java.util.List;

public class ThrowsValidationHandler implements ValidationHandler {

    @Override
    public ValidationHandler append(final DomainError anDomainError) {
        throw DomainException.with(anDomainError);
    }

    @Override
    public ValidationHandler append(final ValidationHandler anHandler) {
        throw DomainException.with(anHandler.getErrors());
    }

    @Override
    public <T> T validate(final Validation<T> aValidation) {
        try {
            return aValidation.validate();
        } catch (final Exception ex) {
            throw DomainException.with(new DomainError(ex.getMessage()));
        }
    }

    @Override
    public List<DomainError> getErrors() {
        return List.of();
    }
}
