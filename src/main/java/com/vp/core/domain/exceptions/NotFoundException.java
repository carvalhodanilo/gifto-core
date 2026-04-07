package com.vp.core.domain.exceptions;

import com.vp.core.domain.AggregateRoot;
import com.vp.core.domain.Identifier;
import com.vp.core.domain.validation.DomainError;

import java.util.Collections;
import java.util.List;

public class NotFoundException extends DomainException {

    protected NotFoundException(final String aMessage, final List<DomainError> anDomainErrors) {
        super(aMessage, anDomainErrors);
    }

    public static NotFoundException with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final Identifier id
    ) {
        final var anError = "%s with ID %s was not found".formatted(
                anAggregate.getSimpleName(),
                id.getValue()
        );
        return new NotFoundException(anError, Collections.emptyList());
    }

    public static NotFoundException with(final DomainError domainError) {
        return new NotFoundException(domainError.message(), List.of(domainError));
    }

    /** Mensagem simples (ex.: recursos públicos sem expor detalhes internos). */
    public static NotFoundException withMessage(final String message) {
        return new NotFoundException(message, Collections.emptyList());
    }
}
