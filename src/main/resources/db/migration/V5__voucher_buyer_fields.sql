-- Dados do comprador na emissão (nulo em vouchers antigos)
alter table vouchers
    add column buyer_name text null,
    add column buyer_phone text null;
