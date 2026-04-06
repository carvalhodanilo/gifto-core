package com.vp.core.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.keycloak.admin")
public record KeycloakAdminProperties(
        String serverUrl,
        String realm,
        String clientId,
        String clientSecret,
        String rolesClientId,
        Provisioning provisioning
) {
    public record Provisioning(
            String initialPassword,
            boolean temporaryPassword
    ) {
    }
}
