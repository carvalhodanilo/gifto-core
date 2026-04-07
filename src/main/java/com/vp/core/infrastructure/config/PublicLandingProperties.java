package com.vp.core.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Landing pública do voucher: resolução do tenant pelo Host (subdomínio).
 * Ex.: host {@code franca-shopping.meudominio.com.br} com sufixo {@code meudominio.com.br} → slug {@code franca-shopping}.
 */
@ConfigurationProperties(prefix = "app.public-landing")
public record PublicLandingProperties(
        /**
         * Sufixo DNS do domínio da landing (sem porta). Vazio = obrigatório usar {@code ?tenantId=} na query.
         */
        String hostSuffix
) {
    public PublicLandingProperties {
        if (hostSuffix == null) {
            hostSuffix = "";
        }
    }
}
