package com.vp.core.application.security;

import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Serviço simples para validação de escopo multi-tenant/multi-merchant.
 *
 * - Roles são validadas via Spring Security (@PreAuthorize).
 * - Aqui a gente só valida coerência do escopo (tenant_id / merchant_id) com base no token e no domínio.
 */
@Service
public class AccessScopeService {

    private final CurrentUserProvider currentUserProvider;
    private final MerchantGateway merchantGateway;

    public AccessScopeService(
            final CurrentUserProvider currentUserProvider,
            final MerchantGateway merchantGateway
    ) {
        this.currentUserProvider = Objects.requireNonNull(currentUserProvider);
        this.merchantGateway = Objects.requireNonNull(merchantGateway);
    }

    @Transactional(readOnly = true)
    public void ensureTenantAccess(final String requestedTenantId) {
        final var user = currentUserProvider.getCurrentUserOrThrow();

        if (user.isSystemAdmin()) {
            // system_admin tem escopo global
            return;
        }

        if (!user.isTenantAdmin() && !user.isTenantOperator()) {
            throw new AccessDeniedException("Acesso negado: role sem permissão para tenant.");
        }

        final var tokenTenantId = user.tenantId();
        if (tokenTenantId == null || tokenTenantId.isBlank()) {
            throw new AccessDeniedException("Acesso negado: tenant_id ausente no token.");
        }

        if (requestedTenantId == null || requestedTenantId.isBlank()) {
            throw new AccessDeniedException("Acesso negado: tenant_id inválido.");
        }

        if (!tokenTenantId.equals(requestedTenantId)) {
            throw new AccessDeniedException("Acesso negado: tenant do token não corresponde ao solicitado.");
        }
    }

    /**
     * Valida que o merchant acessado pertence ao escopo do usuário autenticado.
     *
     * - Para merchant_admin/merchant_operator: merchantId deve bater com merchant_id do token.
     * - Para tenant_admin/tenant_operator: merchantId deve pertencer ao tenant_id do token (lookup no domínio).
     */
    @Transactional(readOnly = true)
    public void ensureMerchantAccess(final String requestedMerchantId) {
        final var user = currentUserProvider.getCurrentUserOrThrow();

        if (user.isSystemAdmin()) {
            return;
        }

        final var tokenTenantId = user.tenantId();
        if (tokenTenantId == null || tokenTenantId.isBlank()) {
            throw new AccessDeniedException("Acesso negado: tenant_id ausente no token.");
        }

        if (user.isMerchantAdmin() || user.isMerchantOperator()) {
            final var tokenMerchantId = user.merchantId();
            if (tokenMerchantId == null || tokenMerchantId.isBlank()) {
                throw new AccessDeniedException("Acesso negado: merchant_id ausente no token.");
            }
            if (!tokenMerchantId.equals(requestedMerchantId)) {
                throw new AccessDeniedException("Acesso negado: merchant do token não corresponde ao solicitado.");
            }
            return;
        }

        // tenant_admin/tenant_operator: merchant precisa pertencer ao tenant do token
        if (user.isTenantAdmin() || user.isTenantOperator()) {
            final MerchantId merchantId = MerchantId.from(requestedMerchantId);
            final Merchant merchant = merchantGateway.findById(merchantId)
                    .orElseThrow(() -> new AccessDeniedException("Acesso negado: merchant não encontrado."))
                    ;

            final var merchantTenantId = merchant.tenantId().getValue();
            if (!tokenTenantId.equals(merchantTenantId)) {
                throw new AccessDeniedException("Acesso negado: merchant não pertence ao tenant do token.");
            }
            return;
        }

        throw new AccessDeniedException("Acesso negado: role sem permissão para merchant.");
    }

    /**
     * Leitura de branding do tenant atual (logo / nome no header) para utilizadores com escopo de shopping.
     * Inclui merchant_admin/merchant_operator: o token já contém o {@code tenant_id} do shopping.
     */
    public void ensureCanReadTenantBranding() {
        final var user = currentUserProvider.getCurrentUserOrThrow();

        if (user.isSystemAdmin()) {
            throw new AccessDeniedException("Acesso negado: branding de tenant não disponível para system_admin.");
        }

        final var tokenTenantId = user.tenantId();
        if (tokenTenantId == null || tokenTenantId.isBlank()) {
            throw new AccessDeniedException("Acesso negado: tenant_id ausente no token.");
        }

        final boolean shoppingScoped = user.isTenantAdmin()
                || user.isTenantOperator()
                || user.isMerchantAdmin()
                || user.isMerchantOperator();
        if (!shoppingScoped) {
            throw new AccessDeniedException("Acesso negado: role sem permissão para branding do tenant.");
        }
    }
}

