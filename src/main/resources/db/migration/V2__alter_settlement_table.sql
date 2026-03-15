alter table voucher_ledger_entries
    add column settlement_entry_id uuid null
    references settlement_entries(id) on delete set null;

create index idx_ledger_settlement_entry_id
    on voucher_ledger_entries (settlement_entry_id);

create index idx_ledger_merchant_created_at
    on voucher_ledger_entries (merchant_id, created_at desc);