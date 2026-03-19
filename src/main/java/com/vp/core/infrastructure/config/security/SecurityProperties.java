package com.vp.core.infrastructure.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        boolean enabled,
        KeycloakProperties keycloak,
        CorsProperties cors
) {
    public record KeycloakProperties(
            String issuerUri,
            String clientId,
            String rolesClientId
    ) {
    }

    public record CorsProperties(
            List<String> allowedOrigins,
            List<String> allowedOriginPatterns,
            List<String> allowedMethods,
            List<String> allowedHeaders,
            List<String> exposedHeaders,
            Boolean allowCredentials,
            Long maxAgeSeconds
    ) {
    }
}
