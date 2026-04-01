# Contrato da API – Campaigns

Documentação do contrato dos endpoints de **Campaign** para integração. Base path: **`/campaigns`**.

---
## Autenticação JWT obrigatória

Os endpoints são protegidos por JWT emitido pelo Keycloak. O client deve enviar:

| Header        | Obrigatório | Descrição |
|---------------|-------------|-----------|
| `Authorization` | Sim | `Bearer <access_token>` |

O backend resolve `tenantId` a partir da claim do token `tenant_id` (e valida coerência via escopo).

---
## 1. Listar campanhas ativas por tenant

Lista apenas campanhas **ativas** do tenant.

### Request

- **Método:** `GET`
- **Path:** `/campaigns`

### Segurança
- Roles: `tenant_admin`, `tenant_operator`

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "campaignList": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "campaignName": "Natal 2026"
    }
  ]
}
```

---
## 2. Criar campaign

### Request

- **Método:** `POST`
- **Path:** `/campaigns`

### Segurança
- Role: `tenant_admin`

### Body (JSON)

```json
{
  "name": "Natal 2026",
  "expirationDays": 90,
  "startsAt": "2026-01-01T00:00:00Z",
  "endsAt": "2026-03-31T23:59:59Z"
}
```

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "campaignId": "660e8400-e29b-41d4-a716-446655440001"
}
```

---
## 3. Listar todas as campanhas por tenant

### Request

- **Método:** `GET`
- **Path:** `/campaigns/all`

### Segurança
- Roles: `tenant_admin`, `tenant_operator`

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "campaignList": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "name": "Natal 2026",
      "expirationDays": 90,
      "startsAt": "2026-01-01T00:00:00Z",
      "endsAt": "2026-03-31T23:59:59Z",
      "status": "ACTIVE"
    }
  ]
}
```

### Valores possíveis de `status`
- `DRAFT`, `ACTIVE`, `PAUSED`, `ENDED`

---
## 4. Atualizar campaign

### Request

- **Método:** `PUT`
- **Path:** `/campaigns/{campaignId}/update`

### Segurança
- Role: `tenant_admin`

### Path parameter

| Nome | Tipo | Descrição |
|------|------|-----------|
| `campaignId` | string | UUID da campaign |

### Body (JSON)

```json
{
  "name": "Natal 2026 (editado)",
  "expirationDays": 100,
  "startsAt": "2026-01-05T00:00:00Z",
  "endsAt": "2026-04-10T23:59:59Z"
}
```

### Response – sucesso
- **Status:** `200 OK`
- **Body:** vazio

---
## 5. Ativar / Pausar / Suspender campaign

### Request

**Ativar**
- **Método:** `PATCH`
- **Path:** `/campaigns/{campaignId}/activate`
- **Segurança:** Role `tenant_admin`

**Pausar**
- **Método:** `PATCH`
- **Path:** `/campaigns/{campaignId}/pause`
- **Segurança:** Role `tenant_admin`

**Suspender**
- **Método:** `PATCH`
- **Path:** `/campaigns/{campaignId}/suspend`
- **Segurança:** Role `tenant_admin`

**Comportamento:** este endpoint **não encerra mais** a campanha. Responde **`422 Unprocessable Entity`** com `message` explicando que exclusão/encerramento não é permitido e que se deve usar **pausar** (`/pause`). Mantido por compatibilidade de clientes antigos; novos fluxos não devem chamá-lo.

### Ativar — regras de período

A ativação só é permitida quando o instante atual (UTC) está **dentro** do intervalo `[startsAt, endsAt]` da campanha (inclusive):

- Antes de `startsAt` → **`422`** com mensagem sobre data de início.
- Depois de `endsAt` → **`422`** com mensagem sobre data de término.

### Response – sucesso
- **Status:** `200 OK`
- **Body:** vazio

---
## Formato padrão de erro

Os erros retornam um JSON com um único campo:

```json
{
  "message": "Descrição legível do erro."
}
```

