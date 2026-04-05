alter table tenants
    add column if not exists logo_url text null;

alter table campaigns
    add column if not exists banner_url text null;

alter table merchants
    add column if not exists landing_logo_url text null;
