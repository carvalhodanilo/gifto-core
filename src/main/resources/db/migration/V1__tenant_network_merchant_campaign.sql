create
extension if not exists "pgcrypto";

-- =========================
-- TENANT (Aggregate)
-- =========================
create table tenants
(
    id                         uuid primary key,
    name                       text        not null,
    fantasy_name               text null,
    document_value             text        not null,
    location_street            text null,
    location_number            text null,
    location_neighborhood      text null,
    location_complement        text null,
    location_city              text null,
    location_state             text null,
    location_country           text null,
    location_postal_code       text null,
    bank_code                  text null,
    bank_name                  text null,
    bank_branch                text null,
    bank_account_number        text null,
    bank_account_digit         text null,
    bank_account_type          text null,
    bank_holder_name           text null,
    bank_holder_document_value text null,
    bank_pix_key_type          text null,
    bank_pix_key_value         text null,
    status                     text        not null,
    phone1                     text null,
    phone2                     text null,
    email                      text        not null,
    url                        text        not null,
    version                    bigint      not null default 0,
    created_at                 timestamptz not null default now(),
    updated_at                 timestamptz not null default now(),

    constraint uq_tenants_document unique (document_value),
    constraint uq_tenants_email unique (email)
);

create index idx_tenants_status on tenants (status);
create index idx_tenants_created_at on tenants (created_at desc);

-- =========================
-- NETWORK
-- =========================
create table networks
(
    id         uuid primary key,
    name       text        not null,
    type       text        not null,
    status     text        not null,
    version    bigint      not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_networks_status on networks (status);
create index idx_networks_type on networks (type);

-- =========================
-- TENANT_NETWORK
-- =========================
create table tenant_networks
(
    tenant_id  uuid        not null references tenants (id) on delete restrict,
    network_id uuid        not null references networks (id) on delete restrict,
    role       text        not null,
    status     text        not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint pk_tenant_networks primary key (tenant_id, network_id)
);

create index idx_tenant_networks_network_id on tenant_networks (network_id);
create index idx_tenant_networks_status on tenant_networks (status);

-- =========================
-- MERCHANT (Aggregate)
-- =========================
create table merchants
(
    id               uuid primary key,
    tenant_id        uuid not null references tenants(id),

    name             text not null,
    fantasy_name     text,
    document_value   text not null,

    location_street       text,
    location_number       text,
    location_neighborhood text,
    location_complement   text,
    location_city         text,
    location_state        text,
    location_country      text,
    location_postal_code  text,

    bank_code                 text,
    bank_name                 text,
    bank_branch               text,
    bank_account_number       text,
    bank_account_digit        text,
    bank_account_type         text,
    bank_holder_name          text,
    bank_holder_document_value text,
    bank_pix_key_type         text,
    bank_pix_key_value        text,

    status           text not null,
    phone1           text,
    phone2           text,
    email            text not null,
    url              text not null,

    version          bigint not null default 0,
    created_at       timestamptz not null default now(),
    updated_at       timestamptz not null default now(),

    constraint uq_merchants_tenant_document unique (tenant_id, document_value),
    constraint uq_merchants_tenant_email unique (tenant_id, email)
);

create index idx_merchants_tenant_id on merchants (tenant_id);
create index idx_merchants_status on merchants (status);
create index idx_merchants_created_at on merchants (created_at desc);

-- =========================
-- MERCHANT_NETWORK (links)
-- Modela Merchant.networkLinks (Map<NetworkId, MerchantNetworkLink>)
-- =========================
create table merchant_networks
(
    merchant_id uuid        not null references merchants (id) on delete restrict,
    network_id  uuid        not null references networks (id) on delete restrict,

    status      text        not null,

    joined_at   timestamptz not null default now(),
    updated_at  timestamptz not null default now(),

    constraint pk_merchant_networks primary key (merchant_id, network_id)
);

create index idx_merchant_networks_network_id on merchant_networks (network_id);
create index idx_merchant_networks_status on merchant_networks (status);
create index idx_merchant_networks_updated_at on merchant_networks (updated_at desc);

-- =========================
-- CAMPAIGN (Aggregate)
-- TENANT creates CAMPAIGN
-- CAMPAIGN targets NETWORK
-- =========================
create table campaigns
(
    id               uuid primary key,

    tenant_id        uuid        not null references tenants (id) on delete restrict,
    network_id       uuid        not null references networks (id) on delete restrict,

    name             text        not null,
    expiration_days  int         not null,

    starts_at        timestamptz not null,
    ends_at          timestamptz not null,

    status           text        not null,

    version          bigint      not null default 0,
    created_at       timestamptz not null default now(),
    updated_at       timestamptz not null default now()
);

create index idx_campaigns_tenant_id on campaigns (tenant_id);
create index idx_campaigns_network_id on campaigns (network_id);
create index idx_campaigns_status on campaigns (status);
create index idx_campaigns_created_at on campaigns (created_at desc);
create index idx_campaigns_starts_at on campaigns (starts_at);
create index idx_campaigns_ends_at on campaigns (ends_at);

-- =========================
-- VOUCHER (Aggregate)
-- =========================
create table vouchers
(
    id              uuid primary key,

    campaign_id     uuid        not null references campaigns (id) on delete restrict,

    token_hash      text        not null,
    token_version   int         not null,
    display_code    text        not null,

    status          text        not null,

    expires_at      timestamptz not null,
    issued_at       timestamptz null,

    version         bigint      not null default 0,
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now(),

    constraint uq_vouchers_token_hash unique (token_hash),
    constraint uq_vouchers_display_code unique (display_code)
);

create index idx_vouchers_campaign_id on vouchers (campaign_id);
create index idx_vouchers_status on vouchers (status);
create index idx_vouchers_expires_at on vouchers (expires_at);
create index idx_vouchers_created_at on vouchers (created_at desc);

-- =========================
-- VOUCHER_LEDGER_ENTRIES
-- =========================
create table voucher_ledger_entries
(
    id                  uuid primary key,

    voucher_id          uuid        not null references vouchers (id) on delete cascade,

    type                text        not null,

    amount_cents        bigint      not null,

    merchant_id         uuid        null references merchants (id) on delete restrict,

    ref_ledger_entry_id uuid        null references voucher_ledger_entries (id) on delete restrict,

    idempotency_key     text        not null,

    created_at          timestamptz not null default now(),

    -- evita duplicação por operação no mesmo voucher
    constraint uq_ledger_voucher_idempotency unique (voucher_id, idempotency_key)
);

create index idx_ledger_voucher_id on voucher_ledger_entries (voucher_id);
create index idx_ledger_voucher_created_at on voucher_ledger_entries (voucher_id, created_at desc);
create index idx_ledger_merchant_id on voucher_ledger_entries (merchant_id);
create index idx_ledger_ref_entry on voucher_ledger_entries (ref_ledger_entry_id);

-- =========================
-- SETTLEMENT_BATCHES
-- =========================
create table settlement_batches
(
    id         uuid primary key,

    tenant_id  uuid        not null references tenants (id) on delete restrict,
    period_key text        not null,

    status     text        not null,
    closed_at  timestamptz null,

    version    bigint      not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_settlement_batches_tenant_id on settlement_batches (tenant_id);
create index idx_settlement_batches_status on settlement_batches (status);
create index idx_settlement_batches_period_key on settlement_batches (period_key);
create index idx_settlement_batches_created_at on settlement_batches (created_at desc);

-- opcional mas altamente recomendado:
create unique index uq_settlement_batches_tenant_period
    on settlement_batches (tenant_id, period_key);

-- =========================
-- SETTLEMENT_ENTRIES
-- =========================
create table settlement_entries
(
    id                   uuid primary key,

    settlement_batch_id  uuid        not null references settlement_batches (id) on delete cascade,
    merchant_id          uuid        not null references merchants (id) on delete restrict,

    gross_cents          bigint      not null,
    reversals_cents      bigint      not null,
    fees_cents           bigint      not null,
    net_cents            bigint      not null,

    status               text        not null,
    paid_at              timestamptz null,
    payment_ref          text        null,

    created_at           timestamptz not null default now(),
    updated_at           timestamptz not null default now()
);

create index idx_settlement_entries_batch_id on settlement_entries (settlement_batch_id);
create index idx_settlement_entries_merchant_id on settlement_entries (merchant_id);
create index idx_settlement_entries_status on settlement_entries (status);
create index idx_settlement_entries_paid_at on settlement_entries (paid_at);

-- =========================
-- USERS
-- =========================
create table users
(
    id           uuid primary key,

    email        text        not null,
    name         text        not null,
    status       text        not null,

    scope_type   text        not null,   -- PLATFORM | TENANT | MERCHANT
    scope_id     uuid        null,       -- null para PLATFORM

    version      bigint      not null default 0,
    created_at   timestamptz not null default now(),
    updated_at   timestamptz not null default now(),

    constraint uq_users_email unique (email)
);

create index idx_users_status on users (status);
create index idx_users_scope_type on users (scope_type);
create index idx_users_scope_id on users (scope_id);
create index idx_users_created_at on users (created_at desc);