package com.vp.core.application.tenant.updateBrandIdentity;

/**
 * Substitui as cores da marca do tenant. Valores null gravam NULL na base (app usa defaults).
 * Formato: #RGB ou #RRGGBB (validado no domínio).
 */
public record UpdateTenantBrandIdentityCommand(
        String tenantId,
        String primaryColor,
        String secondaryColor
) {
}
