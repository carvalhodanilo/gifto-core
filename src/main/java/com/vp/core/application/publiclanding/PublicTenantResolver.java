package com.vp.core.application.publiclanding;

import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.infrastructure.config.PublicLandingProperties;
import com.vp.core.infrastructure.tenant.persistence.TenantJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PublicTenantResolver {

    private final TenantJpaRepository tenantJpaRepository;
    private final PublicLandingProperties publicLandingProperties;

    public PublicTenantResolver(
            final TenantJpaRepository tenantJpaRepository,
            final PublicLandingProperties publicLandingProperties
    ) {
        this.tenantJpaRepository = tenantJpaRepository;
        this.publicLandingProperties = publicLandingProperties;
    }

    /**
     * Resolve o tenant: query {@code tenantId} tem prioridade (sandbox / IP); senão Host + {@code app.public-landing.host-suffix}.
     */
    public UUID resolve(final Optional<UUID> tenantIdParam, final String rawHostHeader) {
        if (tenantIdParam.isPresent()) {
            final var id = tenantIdParam.get();
            if (!tenantJpaRepository.existsById(id)) {
                throw NotFoundException.withMessage("Shopping não encontrado.");
            }
            return id;
        }

        final String host = stripPort(rawHostHeader);
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException(
                    "Cabeçalho Host ausente. Informe o parâmetro tenantId (UUID) na URL ou acesse pelo domínio do shopping."
            );
        }

        final String configuredSuffix = publicLandingProperties.hostSuffix();
        if (configuredSuffix == null || configuredSuffix.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o parâmetro tenantId (UUID) ou configure app.public-landing.host-suffix para usar subdomínio."
            );
        }

        final String suffix = configuredSuffix.toLowerCase().trim();
        final String h = host.toLowerCase();
        if (!h.endsWith(suffix)) {
            throw new IllegalArgumentException("Host não corresponde ao domínio configurado da landing.");
        }

        String prefix = h.substring(0, h.length() - suffix.length());
        if (prefix.endsWith(".")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        if (prefix.isBlank()) {
            throw new IllegalArgumentException("Subdomínio do shopping ausente no Host.");
        }

        final String slug = prefix;
        return tenantJpaRepository.findByPublicSlugIgnoreCase(slug)
                .map(t -> t.getId())
                .orElseThrow(() -> NotFoundException.withMessage("Shopping não encontrado."));
    }

    private static String stripPort(final String rawHostHeader) {
        if (rawHostHeader == null) {
            return null;
        }
        final String t = rawHostHeader.trim();
        final int colon = t.indexOf(':');
        if (colon > 0) {
            return t.substring(0, colon);
        }
        return t;
    }
}
