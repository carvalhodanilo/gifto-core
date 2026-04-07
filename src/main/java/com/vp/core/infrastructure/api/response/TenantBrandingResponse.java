package com.vp.core.infrastructure.api.response;

/**
 * DTO mínimo para header / tema: shopping (tenant) do utilizador autenticado.
 */
public record TenantBrandingResponse(
        String tenantId,
        String name,
        String logoUrl
) {
    public static TenantBrandingResponse of(
            final String tenantId,
            final String name,
            final String logoUrl
    ) {
        return new TenantBrandingResponse(tenantId, name, logoUrl);
    }
}
