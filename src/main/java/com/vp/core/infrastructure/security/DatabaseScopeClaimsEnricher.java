package com.vp.core.infrastructure.security;

import com.vp.core.application.security.AuthenticatedUser;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.user.User;
import com.vp.core.infrastructure.user.model.UserJpaEntity;
import com.vp.core.infrastructure.user.persistence.UserJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Keycloak (sobretudo com user profile declarativo) pode não incluir tenant_id/merchant_id no access token
 * apesar dos protocol mappers. A API exige essas claims para o {@link com.vp.core.application.security.AccessScopeService}.
 * Aqui completamos a partir da tabela {@code users} (email do JWT), alinhada ao provisionamento do core.
 */
@Component
public class DatabaseScopeClaimsEnricher {

    private final UserJpaRepository userJpaRepository;
    private final MerchantGateway merchantGateway;

    public DatabaseScopeClaimsEnricher(
            final UserJpaRepository userJpaRepository,
            final MerchantGateway merchantGateway
    ) {
        this.userJpaRepository = userJpaRepository;
        this.merchantGateway = merchantGateway;
    }

    public AuthenticatedUser enrich(final AuthenticatedUser principal) {
        if (principal.isSystemAdmin()) {
            return principal;
        }
        final boolean roleNeedsScope = principal.isTenantAdmin()
                || principal.isTenantOperator()
                || principal.isMerchantAdmin()
                || principal.isMerchantOperator();
        if (!roleNeedsScope) {
            return principal;
        }
        final boolean missingTenant = principal.tenantId() == null || principal.tenantId().isBlank();
        final boolean missingMerchant = (principal.isMerchantAdmin() || principal.isMerchantOperator())
                && (principal.merchantId() == null || principal.merchantId().isBlank());
        if (!missingTenant && !missingMerchant) {
            return principal;
        }
        final String email = principal.email();
        if (email == null || email.isBlank()) {
            return principal;
        }
        return userJpaRepository.findByEmail(email)
                .map(UserJpaEntity::toAggregate)
                .map(u -> applyScopeFromAggregate(u, principal))
                .orElse(principal);
    }

    private AuthenticatedUser applyScopeFromAggregate(final User user, final AuthenticatedUser principal) {
        final var scope = user.getScope();
        if (scope.isPlatform()) {
            return principal;
        }
        String tenantId = principal.tenantId();
        String merchantId = principal.merchantId();
        if (scope.isTenant()) {
            tenantId = scope.getScopeId();
        } else if (scope.isMerchant()) {
            merchantId = scope.getScopeId();
            tenantId = merchantGateway.findById(MerchantId.from(merchantId))
                    .map(m -> m.tenantId().getValue())
                    .orElse(tenantId);
        }
        return principal.withScopeIds(tenantId, merchantId);
    }
}
