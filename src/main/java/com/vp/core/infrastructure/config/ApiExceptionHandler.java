package com.vp.core.infrastructure.config;

import com.vp.core.domain.exceptions.DomainException;
import com.vp.core.domain.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(final NotFoundException ex) {
        final var message = ex.getDomainErrors().isEmpty()
                ? ex.getMessage()
                : ex.getDomainErrors().get(0).message();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(final IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(final IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> handleDomain(final DomainException ex) {
        final var message = ex.getDomainErrors().isEmpty()
                ? ex.getMessage()
                : ex.getDomainErrors().get(0).message();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("message", message));
    }
}
