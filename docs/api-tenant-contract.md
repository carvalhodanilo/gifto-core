# Contrato da API – Tenants

Documentação do contrato dos endpoints de **Tenant** para integração. Base path: **`/tenants`**.

---
## Autenticação JWT obrigatória

Os endpoints são protegidos por JWT emitido pelo Keycloak. O client deve enviar:

| Header        | Obrigatório | Descrição |
|---------------|-------------|-----------|
| `Authorization` | Sim | `Bearer <access_token>` |

---
## 1. Criar tenant

Cria um novo tenant no sistema.

### Request

- **Método:** `POST`
- **Path:** `/tenants`

### Segurança
- Roles: `system_admin`

### Body (JSON)

```json
{
  "name": "Tenant 1",
  "fantasyName": "Tenant 1 Fantasy",
  "document": "12.345.678/0001-00",
  "phone1": "11999990000",
  "email": "tenant1@local.test",
  "url": "https://tenant1.example.com"
}
```

### Response – sucesso

- **Status:** `201 Created`
- **Header:** `Location: /tenants/{tenantId}`
- **Body (JSON):**

```json
{
  "tenantId": "57681649-b182-49fd-904e-19adcbfa3ada"
}
```

---
## 2. Listar tenants ativos

Retorna a lista de tenants.

### Request

- **Método:** `GET`
- **Path:** `/tenants`

### Segurança
- Roles: `system_admin`

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "tenants": [
    {
      "id": "57681649-b182-49fd-904e-19adcbfa3ada",
      "fantasyName": "Tenant 1 Fantasy"
    }
  ]
}
```

### Erros

| Status | Quando | Body (JSON) |
|--------|--------|-------------|
| `403 Forbidden` | usuário sem role `system_admin` | `{ "message": "<descrição do erro>" }` |

---
## Branding do tenant da sessão (header / apps)

Nome de exibição e URL do logo do shopping associado ao `tenant_id` do JWT.

### Request

- **Método:** `GET`
- **Path:** `/tenants/me/branding`

### Segurança

- Roles: `tenant_admin`, `tenant_operator`, `merchant_admin`, `merchant_operator`
- O `tenant_id` é obtido **apenas** do token (sem path param; evita IDOR).

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "tenantId": "57681649-b182-49fd-904e-19adcbfa3ada",
  "name": "Nome fantasia ou razão social",
  "logoUrl": "https://..."
}
```

`logoUrl` pode ser `null` se não houver logo carregado.

### Erros

| Status | Quando |
|--------|--------|
| `403 Forbidden` | `system_admin`, role sem escopo shopping, ou `tenant_id` ausente no token |

---
## Formato padrão de erro

Os erros retornam um JSON com um único campo:

```json
{
  "message": "Descrição legível do erro."
}
```

