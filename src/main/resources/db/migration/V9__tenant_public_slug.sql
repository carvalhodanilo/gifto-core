-- Slug público para resolver o shopping a partir do subdomínio (ex.: franca-shopping.meudominio.com.br).
-- Opcional: em sandbox com IP, use query param tenantId no endpoint público.
ALTER TABLE tenants
    ADD COLUMN IF NOT EXISTS public_slug text NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_tenants_public_slug
    ON tenants (public_slug)
    WHERE public_slug IS NOT NULL AND trim(public_slug) <> '';
