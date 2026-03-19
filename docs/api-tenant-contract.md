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
## Formato padrão de erro

Os erros retornam um JSON com um único campo:

```json
{
  "message": "Descrição legível do erro."
}
```

