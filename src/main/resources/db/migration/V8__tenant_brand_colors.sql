-- Cores da marca (opcionais). NULL = app usa paleta padrão (ex. mock do sales-app).
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS primary_brand_color VARCHAR(16);
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS secondary_brand_color VARCHAR(16);
