package com.vp.core.infrastructure.api.request;

/**
 * Cores da marca. Ambos opcionais no JSON; null em cada campo grava NULL (fallback para defaults da app).
 */
public record UpdateTenantBrandIdentityRequest(
        String primaryColor,
        String secondaryColor
) {
}
