-- Seed de dados de desenvolvimento (MVP)
-- - 2 tenants
-- - 2 merchants por tenant (total 4)
-- - 1 network default por tenant + links de merchants na network
-- - usuários internos (tabela users) por escopo (inclui system_admin no escopo PLATFORM)
--
-- Observações:
-- - Os IDs (tenant_id/merchant_id/user_id) são fixos para facilitar testes manuais.
-- - Se algum tenant/merchant/user com esses IDs já existir, o insert é ignorado via ON CONFLICT.
-- - Após os inserts, o script valida que todos os IDs exigidos existem; se não, falha com mensagem clara.

DO $$
DECLARE
  -- TENANTS
  v_tenant_1 uuid := '57681649-b182-49fd-904e-19adcbfa3ada';
  v_tenant_2 uuid := '8b4d1c2e-1b4c-4f3b-93e9-8c0f3f55c0f0';

  -- MERCHANTS (2 por tenant)
  v_merchant_11 uuid := 'cd13011f-5bde-4f3b-92b1-3a99845e2f41';
  v_merchant_12 uuid := '9a8ff1e3-0b3b-4d7e-8f4b-1fbf5b5b4c0a';
  v_merchant_21 uuid := 'd0c8d6b1-9c74-4df1-8dd6-ef0b5d6d4b9c';
  v_merchant_22 uuid := '3d4ccf0a-2f6d-4a7a-9d7b-7f6a8a4c5d1e';

  -- NETWORKS (default por tenant)
  v_network_1 uuid := '55555555-5555-4555-8555-555555555551';
  v_network_2 uuid := '55555555-5555-4555-8555-555555555552';

  -- USERS (id fixo para facilitar debug)
  -- Platform user (system admin)
  v_user_platform_system_admin uuid := '33333333-3333-4333-8333-333333333331';

  -- Tenant users
  v_user_tenant1_admin uuid := '11111111-1111-4111-8111-111111111111';
  v_user_tenant1_operator uuid := '11111111-1111-4111-8111-111111111112';
  v_user_tenant2_admin uuid := '11111111-1111-4111-8111-111111111113';
  v_user_tenant2_operator uuid := '11111111-1111-4111-8111-111111111114';

  -- Merchant users (2 por merchant)
  v_user_merchant11_admin uuid := '22222222-2222-4222-8222-222222222221';
  v_user_merchant11_operator uuid := '22222222-2222-4222-8222-222222222222';
  v_user_merchant12_admin uuid := '22222222-2222-4222-8222-222222222223';
  v_user_merchant12_operator uuid := '22222222-2222-4222-8222-222222222224';
  v_user_merchant21_admin uuid := '22222222-2222-4222-8222-222222222225';
  v_user_merchant21_operator uuid := '22222222-2222-4222-8222-222222222226';
  v_user_merchant22_admin uuid := '22222222-2222-4222-8222-222222222227';
  v_user_merchant22_operator uuid := '22222222-2222-4222-8222-222222222228';

  v_missing text := '';
BEGIN
  -- =========================
  -- TENANTS
  -- =========================
  INSERT INTO tenants (id, name, fantasy_name, document_value, status, email, url)
  VALUES
    (v_tenant_1, 'Tenant 1', 'Tenant 1', 'DOC-TENANT-1', 'ACTIVE', 'tenant.admin@local.test', 'http://localhost/tenants/1'),
    (v_tenant_2, 'Tenant 2', 'Tenant 2', 'DOC-TENANT-2', 'ACTIVE', 'tenant2.admin@local.test', 'http://localhost/tenants/2')
  ON CONFLICT (id) DO NOTHING;

  -- =========================
  -- MERCHANTS
  -- =========================
  INSERT INTO merchants (id, tenant_id, name, fantasy_name, document_value, status, email, url)
  VALUES
    (v_merchant_11, v_tenant_1, 'Merchant 1.1', 'Merchant 1.1', 'DOC-MERCHANT-1-1', 'ACTIVE', 'merchant.admin@local.test', 'http://localhost/merchants/1-1'),
    (v_merchant_12, v_tenant_1, 'Merchant 1.2', 'Merchant 1.2', 'DOC-MERCHANT-1-2', 'ACTIVE', 'merchant2.admin@local.test', 'http://localhost/merchants/1-2'),
    (v_merchant_21, v_tenant_2, 'Merchant 2.1', 'Merchant 2.1', 'DOC-MERCHANT-2-1', 'ACTIVE', 'merchant3.admin@local.test', 'http://localhost/merchants/2-1'),
    (v_merchant_22, v_tenant_2, 'Merchant 2.2', 'Merchant 2.2', 'DOC-MERCHANT-2-2', 'ACTIVE', 'merchant4.admin@local.test', 'http://localhost/merchants/2-2')
  ON CONFLICT (id) DO NOTHING;

  -- =========================
  -- NETWORKS + LINKS
  -- =========================
  -- Network default por tenant (PRIVATE, ACTIVE)
  INSERT INTO networks (id, name, type, status)
  VALUES
    (v_network_1, 'DEFAULT', 'PRIVATE', 'ACTIVE'),
    (v_network_2, 'DEFAULT', 'PRIVATE', 'ACTIVE')
  ON CONFLICT (id) DO NOTHING;

  -- Tenant host membership na network
  INSERT INTO tenant_networks (tenant_id, network_id, role, status)
  VALUES
    (v_tenant_1, v_network_1, 'HOST', 'ACTIVE'),
    (v_tenant_2, v_network_2, 'HOST', 'ACTIVE')
  ON CONFLICT (tenant_id, network_id) DO NOTHING;

  -- Cada merchant ligado na network default do seu tenant
  INSERT INTO merchant_networks (merchant_id, network_id, status)
  VALUES
    (v_merchant_11, v_network_1, 'ACTIVE'),
    (v_merchant_12, v_network_1, 'ACTIVE'),
    (v_merchant_21, v_network_2, 'ACTIVE'),
    (v_merchant_22, v_network_2, 'ACTIVE')
  ON CONFLICT (merchant_id, network_id) DO NOTHING;

  -- =========================
  -- USERS (tabela users)
  -- =========================
  -- Platform scope user (system_admin)
  INSERT INTO users (id, email, name, status, scope_type, scope_id)
  VALUES
    (v_user_platform_system_admin, 'system.admin@local.test', 'System - Admin', 'ACTIVE', 'PLATFORM', null)
  ON CONFLICT (id) DO NOTHING;

  -- Tenant scope users
  INSERT INTO users (id, email, name, status, scope_type, scope_id)
  VALUES
    (v_user_tenant1_admin, 'tenant.admin@local.test', 'Tenant 1 - Admin', 'ACTIVE', 'TENANT', v_tenant_1),
    (v_user_tenant1_operator, 'tenant.operator@local.test', 'Tenant 1 - Operator', 'ACTIVE', 'TENANT', v_tenant_1),
    (v_user_tenant2_admin, 'tenant2.admin@local.test', 'Tenant 2 - Admin', 'ACTIVE', 'TENANT', v_tenant_2),
    (v_user_tenant2_operator, 'tenant2.operator@local.test', 'Tenant 2 - Operator', 'ACTIVE', 'TENANT', v_tenant_2)
  ON CONFLICT (id) DO NOTHING;

  -- Merchant scope users
  INSERT INTO users (id, email, name, status, scope_type, scope_id)
  VALUES
    (v_user_merchant11_admin, 'merchant.admin@local.test', 'Merchant 1.1 - Admin', 'ACTIVE', 'MERCHANT', v_merchant_11),
    (v_user_merchant11_operator, 'merchant.operator@local.test', 'Merchant 1.1 - Operator', 'ACTIVE', 'MERCHANT', v_merchant_11),
    (v_user_merchant12_admin, 'merchant2.admin@local.test', 'Merchant 1.2 - Admin', 'ACTIVE', 'MERCHANT', v_merchant_12),
    (v_user_merchant12_operator, 'merchant2.operator@local.test', 'Merchant 1.2 - Operator', 'ACTIVE', 'MERCHANT', v_merchant_12),
    (v_user_merchant21_admin, 'merchant3.admin@local.test', 'Merchant 2.1 - Admin', 'ACTIVE', 'MERCHANT', v_merchant_21),
    (v_user_merchant21_operator, 'merchant3.operator@local.test', 'Merchant 2.1 - Operator', 'ACTIVE', 'MERCHANT', v_merchant_21),
    (v_user_merchant22_admin, 'merchant4.admin@local.test', 'Merchant 2.2 - Admin', 'ACTIVE', 'MERCHANT', v_merchant_22),
    (v_user_merchant22_operator, 'merchant4.operator@local.test', 'Merchant 2.2 - Operator', 'ACTIVE', 'MERCHANT', v_merchant_22)
  ON CONFLICT (id) DO NOTHING;

  -- =========================
  -- VALIDAÇÕES (ids esperados existem)
  -- =========================
  IF NOT EXISTS (SELECT 1 FROM tenants WHERE id = v_tenant_1) THEN
    v_missing := v_missing || format('tenant_id missing: %s; ', v_tenant_1);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM tenants WHERE id = v_tenant_2) THEN
    v_missing := v_missing || format('tenant_id missing: %s; ', v_tenant_2);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM merchants WHERE id = v_merchant_11) THEN
    v_missing := v_missing || format('merchant_id missing: %s; ', v_merchant_11);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM merchants WHERE id = v_merchant_12) THEN
    v_missing := v_missing || format('merchant_id missing: %s; ', v_merchant_12);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM merchants WHERE id = v_merchant_21) THEN
    v_missing := v_missing || format('merchant_id missing: %s; ', v_merchant_21);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM merchants WHERE id = v_merchant_22) THEN
    v_missing := v_missing || format('merchant_id missing: %s; ', v_merchant_22);
  END IF;

  -- NETWORK validations
  IF NOT EXISTS (SELECT 1 FROM networks WHERE id = v_network_1) THEN
    v_missing := v_missing || format('network_id missing: %s; ', v_network_1);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM networks WHERE id = v_network_2) THEN
    v_missing := v_missing || format('network_id missing: %s; ', v_network_2);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM merchant_networks
    WHERE merchant_id = v_merchant_11 AND network_id = v_network_1
  ) THEN
    v_missing := v_missing || 'merchant_network link missing: m11->n1; ';
  END IF;
  IF NOT EXISTS (
    SELECT 1 FROM merchant_networks
    WHERE merchant_id = v_merchant_12 AND network_id = v_network_1
  ) THEN
    v_missing := v_missing || 'merchant_network link missing: m12->n1; ';
  END IF;
  IF NOT EXISTS (
    SELECT 1 FROM merchant_networks
    WHERE merchant_id = v_merchant_21 AND network_id = v_network_2
  ) THEN
    v_missing := v_missing || 'merchant_network link missing: m21->n2; ';
  END IF;
  IF NOT EXISTS (
    SELECT 1 FROM merchant_networks
    WHERE merchant_id = v_merchant_22 AND network_id = v_network_2
  ) THEN
    v_missing := v_missing || 'merchant_network link missing: m22->n2; ';
  END IF;

  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_tenant1_admin) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_tenant1_admin);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_tenant1_operator) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_tenant1_operator);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_platform_system_admin) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_platform_system_admin);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_tenant2_admin) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_tenant2_admin);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_tenant2_operator) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_tenant2_operator);
  END IF;

  -- Merchant users
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_merchant11_admin) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_merchant11_admin);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_merchant11_operator) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_merchant11_operator);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_merchant12_admin) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_merchant12_admin);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_merchant12_operator) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_merchant12_operator);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_merchant21_admin) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_merchant21_admin);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_merchant21_operator) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_merchant21_operator);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_merchant22_admin) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_merchant22_admin);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = v_user_merchant22_operator) THEN
    v_missing := v_missing || format('user_id missing: %s; ', v_user_merchant22_operator);
  END IF;

  IF v_missing IS NOT NULL AND length(trim(v_missing)) > 0 THEN
    RAISE EXCEPTION 'Seed demo failed. Missing ids: %', v_missing;
  END IF;
END $$;

