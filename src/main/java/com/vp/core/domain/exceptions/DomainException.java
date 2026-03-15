package com.vp.core.domain.exceptions;

import com.vp.core.domain.validation.DomainError;

import java.util.List;

public class DomainException extends RuntimeException  {

    protected final List<DomainError> domainErrors;

    protected DomainException(final String aMessage, final List<DomainError> anDomainErrors) {
        super(aMessage);
        this.domainErrors = anDomainErrors;
    }

    public static DomainException with(final DomainError anErrors) {
        return new DomainException(anErrors.message(), List.of(anErrors));
    }

    public static DomainException with(final String message, final DomainError anErrors) {
        return new DomainException(message, List.of(anErrors));
    }

    public static DomainException with(final List<DomainError> anDomainErrors) {
        return new DomainException("", anDomainErrors);
    }

    public List<DomainError> getDomainErrors() {
        return domainErrors;
    }
}
