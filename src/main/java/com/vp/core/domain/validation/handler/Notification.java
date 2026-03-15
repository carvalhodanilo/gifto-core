package com.vp.core.domain.validation.handler;

import com.vp.core.domain.exceptions.DomainException;
import com.vp.core.domain.exceptions.NotificationException;
import com.vp.core.domain.validation.DomainError;
import com.vp.core.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class Notification implements ValidationHandler {

    private final List<DomainError> domainErrors;

    private Notification(final List<DomainError> domainErrors) {
        this.domainErrors = domainErrors;
    }

    public static Notification create() {
        return new Notification(new ArrayList<>());
    }

    public static Notification create(final Throwable t) {
        return create(new DomainError(t.getMessage()));
    }

    public static Notification create(final DomainError anDomainError) {
        return new Notification(new ArrayList<>()).append(anDomainError);
    }

    @Override
    public Notification append(final DomainError anDomainError) {
        this.domainErrors.add(anDomainError);
        return this;
    }

    @Override
    public Notification append(final ValidationHandler anHandler) {
        this.domainErrors.addAll(anHandler.getErrors());
        return this;
    }

    @Override
    public <T> T validate(final Validation<T> aValidation) {
        try {
            return aValidation.validate();
        } catch (final DomainException ex) {
            this.domainErrors.addAll(ex.getDomainErrors());
        } catch (final Throwable t) {
            this.domainErrors.add(new DomainError(t.getMessage()));
        }
        return null;
    }

    @Override
    public List<DomainError> getErrors() {
        return this.domainErrors;
    }

    public void notifyIfHasError(String message) {
        if (this.hasError()) throw new NotificationException(message, this);
    }
}
