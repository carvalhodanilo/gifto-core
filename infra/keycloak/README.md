# Keycloak (dev local) — Gifto

Setup local e reproduzível de autenticação/autorização via Keycloak, com:
- 1 realm (`gifto`)
- 3 clients (API + 2 web)
- roles do sistema
- claims customizadas `tenant_id` e `merchant_id` (a partir de atributos do usuário)
- usuários de teste

## Como subir

1) (Opcional) criar seu `.env` a partir do exemplo:

```bash
cp infra/keycloak/.env.example infra/keycloak/.env
```

2) Subir:

```bash
docker compose --env-file infra/keycloak/.env -f infra/keycloak/docker-compose.yaml up -d
```

3) Acessar o admin console:
- URL: `http://localhost:8081/`
- Admin user: `admin`
- Admin password: `admin`

O realm será importado automaticamente na primeira subida (via `--import-realm`).

### Reimport / reset (quando mudar o JSON do realm)

Por padrão, após o primeiro boot o Keycloak **não sobrescreve** um realm já existente no banco.
Então, se você editar `infra/keycloak/realm/realm-gifto.json` e quiser reimportar do zero:

```bash
docker compose --env-file infra/keycloak/.env -f infra/keycloak/docker-compose.yaml down -v
docker compose --env-file infra/keycloak/.env -f infra/keycloak/docker-compose.yaml up -d
```

## Realm, clients, roles

- **Realm**: `gifto`
- **Clients**:
  - `voucher-platform-api` (API / resource server)
  - `voucher-platform-admin-web` (web/admin)
  - `voucher-platform-sales-web` (web/sales)
- **Roles do sistema** (definidas como *client roles* no client `voucher-platform-api`):
  - `system_admin`
  - `tenant_admin`
  - `tenant_operator` (venda de vouchers no escopo do tenant)
  - `merchant_admin`
  - `merchant_operator`
- **Client `gifto-core-admin`** (confidencial, *service account*): usado pelo backend Spring para criar utilizadores no realm via Admin API. O *service account* tem roles no client `realm-management` (`manage-users`, `view-users`, `query-users`). O secret por defeito no JSON de import é `core-admin-dev-secret-change-me` — **altera em produção** e define `KEYCLOAK_ADMIN_CLIENT_SECRET` no ambiente do core.

### Onde as roles aparecem no token

Como são *client roles*, elas aparecem em:
- `resource_access.voucher-platform-api.roles`

## Backend (core): provisionamento de utilizadores

Ao criar **tenant** ou **merchant**, o core cria o utilizador convidado no Keycloak e grava a linha na tabela `users`. Variáveis relevantes (além do JWT/resource server):

| Variável | Descrição |
|----------|-----------|
| `KEYCLOAK_ADMIN_SERVER_URL` | Raiz HTTP do Keycloak (ex.: `http://localhost:8081` ou `http://keycloak:8080/auth` com `KC_HTTP_RELATIVE_PATH=/auth`) |
| `KEYCLOAK_ADMIN_REALM` | Realm (defeito: `gifto`) |
| `KEYCLOAK_ADMIN_CLIENT_ID` | Client confidencial (defeito: `gifto-core-admin`) |
| `KEYCLOAK_ADMIN_CLIENT_SECRET` | Secret do client (deve coincidir com o realm) |
| `KEYCLOAK_USER_INITIAL_PASSWORD` | Senha inicial enviada ao Keycloak (o Keycloak **não gera** senha na API) |
| `KEYCLOAK_USER_TEMPORARY_PASSWORD` | Defeito recomendado MVP: `false` (senha definitiva, sem troca obrigatória). `true` ativa credencial temporária + `UPDATE_PASSWORD` no primeiro login |

Comportamento **MVP** no código (`KeycloakUserProvisioner`): `emailVerified=true`, sem *required actions* (ex.: sem `VERIFY_EMAIL`). Comentários no código indicam onde reativar confirmação de email e senha temporária.

**Realm já importado:** se adicionares o client `gifto-core-admin` ao JSON e o realm existir na base do Keycloak, o import **não** aplica alterações por defeito. Atribui manualmente as roles de `realm-management` ao *service account* ou recria o volume do Keycloak (ver secção de reimport acima).

## Claims customizadas no token

As claims são mapeadas a partir de atributos do usuário:
- `tenant_id`  ← atributo do usuário `tenant_id` (quando existir)
- `merchant_id` ← atributo do usuário `merchant_id` (quando existir)

Observações:
- Para `system_admin` normalmente não existe `tenant_id`/`merchant_id`.
- Para `tenant_admin` existe `tenant_id`.
- Para `tenant_operator` existe `tenant_id` e normalmente não existe `merchant_id`.
- Para `merchant_admin` e `merchant_operator` existem `tenant_id` e `merchant_id`.
- Se um atributo não existir, a claim tende a não aparecer no token (isso é esperado).

## Usuários de teste

Senha padrão (todos): `Local123!`

- **Variável de conveniência**: `GIFTO_TEST_USERS_PASSWORD`
  - **Importante**: o arquivo de import `realm-*.json` **não lê** env vars, então mudar essa variável **não altera** automaticamente a senha dos usuários.
  - Se quiser trocar a senha, altere também os `credentials.value` em `infra/keycloak/realm/realm-gifto.json` e faça o reset (`down -v` / `up -d`).

- **System Admin**
  - login/email: `system.admin@local.test`
  - role: `system_admin`
- **Tenant Admin**
  - login/email: `tenant.admin@local.test`
  - role: `tenant_admin`
  - tenant_id: `57681649-b182-49fd-904e-19adcbfa3ada`
- **Tenant Operator**
  - login/email: `tenant.operator@local.test`
  - role: `tenant_operator`
  - tenant_id: `57681649-b182-49fd-904e-19adcbfa3ada`
- **Tenant 2 Admin**
  - login/email: `tenant2.admin@local.test`
  - role: `tenant_admin`
  - tenant_id: `8b4d1c2e-1b4c-4f3b-93e9-8c0f3f55c0f0`
- **Tenant 2 Operator**
  - login/email: `tenant2.operator@local.test`
  - role: `tenant_operator`
  - tenant_id: `8b4d1c2e-1b4c-4f3b-93e9-8c0f3f55c0f0`
- **Merchant Admin**
  - login/email: `merchant.admin@local.test`
  - role: `merchant_admin`
  - tenant_id: `57681649-b182-49fd-904e-19adcbfa3ada`
  - merchant_id: `cd13011f-5bde-4f3b-92b1-3a99845e2f41`
- **Merchant Operator**
  - login/email: `merchant.operator@local.test`
  - role: `merchant_operator`
  - tenant_id: `57681649-b182-49fd-904e-19adcbfa3ada`
  - merchant_id: `cd13011f-5bde-4f3b-92b1-3a99845e2f41`
- **Merchant 2 Admin**
  - login/email: `merchant2.admin@local.test`
  - role: `merchant_admin`
  - tenant_id: `57681649-b182-49fd-904e-19adcbfa3ada`
  - merchant_id: `9a8ff1e3-0b3b-4d7e-8f4b-1fbf5b5b4c0a`
- **Merchant 2 Operator**
  - login/email: `merchant2.operator@local.test`
  - role: `merchant_operator`
  - tenant_id: `57681649-b182-49fd-904e-19adcbfa3ada`
  - merchant_id: `9a8ff1e3-0b3b-4d7e-8f4b-1fbf5b5b4c0a`
- **Merchant 3 Admin**
  - login/email: `merchant3.admin@local.test`
  - role: `merchant_admin`
  - tenant_id: `8b4d1c2e-1b4c-4f3b-93e9-8c0f3f55c0f0`
  - merchant_id: `d0c8d6b1-9c74-4df1-8dd6-ef0b5d6d4b9c`
- **Merchant 3 Operator**
  - login/email: `merchant3.operator@local.test`
  - role: `merchant_operator`
  - tenant_id: `8b4d1c2e-1b4c-4f3b-93e9-8c0f3f55c0f0`
  - merchant_id: `d0c8d6b1-9c74-4df1-8dd6-ef0b5d6d4b9c`
- **Merchant 4 Admin**
  - login/email: `merchant4.admin@local.test`
  - role: `merchant_admin`
  - tenant_id: `8b4d1c2e-1b4c-4f3b-93e9-8c0f3f55c0f0`
  - merchant_id: `3d4ccf0a-2f6d-4a7a-9d7b-7f6a8a4c5d1e`
- **Merchant 4 Operator**
  - login/email: `merchant4.operator@local.test`
  - role: `merchant_operator`
  - tenant_id: `8b4d1c2e-1b4c-4f3b-93e9-8c0f3f55c0f0`
  - merchant_id: `3d4ccf0a-2f6d-4a7a-9d7b-7f6a8a4c5d1e`

## Como obter um token (para testes)

Para facilitar o dev local, os clients web estão com **Direct Access Grants** habilitado.

Exemplo com `tenant.admin@local.test` usando o client `voucher-platform-admin-web` (public client):

```bash
curl -s \
  -X POST "http://localhost:8081/realms/gifto/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=voucher-platform-admin-web" \
  -d "username=tenant.admin@local.test" \
  -d "password=${GIFTO_TEST_USERS_PASSWORD:-Local123!}" | python3 - <<'PY'
import json,sys
print(json.load(sys.stdin)["access_token"])
PY
```

Para inspecionar o payload do JWT:

```bash
TOKEN="$(curl -s -X POST "http://localhost:8081/realms/gifto/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=voucher-platform-admin-web" \
  -d "username=merchant.admin@local.test" \
  -d "password=${GIFTO_TEST_USERS_PASSWORD:-Local123!}" | python3 - <<'PY'
import json,sys
print(json.load(sys.stdin)["access_token"])
PY
)"

TOKEN="$TOKEN" python3 - <<'PY'
import base64, json, os
token = os.environ["TOKEN"]
payload_b64 = token.split(".")[1].encode()
payload_b64 += b"=" * (-len(payload_b64) % 4)
print(json.dumps(json.loads(base64.urlsafe_b64decode(payload_b64).decode()), indent=2, sort_keys=True))
PY
```

## Integração com Spring Boot (resource server)

Use:
- **Issuer URI**: `http://localhost:8081/realms/gifto`

Exemplo de propriedades:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8081/realms/gifto
```

Onde ler roles no backend:
- `resource_access.voucher-platform-api.roles`

Claims de escopo:
- `tenant_id`
- `merchant_id`

