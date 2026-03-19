package com.vp.core.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Map;

public final class SecurityErrorHandlers {

    private SecurityErrorHandlers() {
    }

    public static AuthenticationEntryPoint authenticationEntryPoint(final ObjectMapper objectMapper) {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
            writeJson(response, objectMapper, HttpServletResponse.SC_UNAUTHORIZED, "Não autenticado.");
        };
    }

    public static AccessDeniedHandler accessDeniedHandler(final ObjectMapper objectMapper) {
        return (HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) -> {
            writeJson(response, objectMapper, HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
        };
    }

    private static void writeJson(
            final HttpServletResponse response,
            final ObjectMapper objectMapper,
            final int status,
            final String message
    ) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Map.of("message", message));
    }
}
