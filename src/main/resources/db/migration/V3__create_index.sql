create index idx_vouchers_campaign_status_issued
    on vouchers (campaign_id, status, issued_at desc);

create index idx_vouchers_display_code
    on vouchers (display_code);