# Contrato da API – Vouchers

Documentação do contrato dos endpoints de **Voucher** para integração. Base path: **`/v1/vouchers`**.

---

## Autenticação JWT obrigatória

| Header   | Obrigatório | Descrição                          |
|----------|-------------|------------------------------------|
| `Authorization` | Sim | `Bearer <access_token>` |

---

## 1. Listar vouchers por tenant (paginado)

Retorna uma lista paginada de vouchers do tenant. Permite filtrar por campanha ativa, nome da campanha e display code.

### Request

- **Método:** `GET`
- **Path:** `/v1/vouchers/list`
- **Headers:** `Authorization: Bearer <access_token>`
- **Query parameters:**

| Nome          | Tipo    | Obrigatório | Default | Descrição |
|---------------|---------|-------------|---------|-----------|
| `page`        | number  | Não         | `0`     | Página (zero-based). |
| `perPage`     | number  | Não         | `10`    | Quantidade por página. |
| `active`      | boolean | Não         | —       | Se `true`, filtra apenas vouchers de campanhas ativas. |
| `campaignName`| string  | Não         | —       | Filtro por nome da campanha (parcial). |
| `displayCode` | string  | Não         | —       | Filtro por código de exibição do voucher. |

Exemplo: `GET /v1/vouchers/list?page=0&perPage=10&active=true` com `Authorization: Bearer <access_token>` (claim `tenant_id` define o tenant).

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "currentPage": 0,
  "perPage": 10,
  "total": 42,
  "items": [
    {
      "voucherId": "550e8400-e29b-41d4-a716-446655440000",
      "campaignId": "660e8400-e29b-41d4-a716-446655440001",
      "campaignName": "Natal 2026",
      "status": "ISSUED",
      "amountCents": 5000,
      "issuedAt": "2026-01-15T10:00:00Z",
      "expiresAt": "2026-12-31T23:59:59Z"
    }
  ]
}
```

| Campo (raiz)  | Tipo   | Descrição |
|---------------|--------|-----------|
| `currentPage` | number | Página atual (zero-based). |
| `perPage`     | number | Tamanho da página. |
| `total`       | number | Total de registros que atendem aos filtros. |
| `items`       | array  | Lista de vouchers. |

Cada objeto em **`items`**:

| Campo         | Tipo   | Descrição |
|---------------|--------|-----------|
| `voucherId`   | string | UUID do voucher. |
| `campaignId`  | string | UUID da campanha. |
| `campaignName`| string | Nome da campanha. |
| `status`      | string | Status do voucher (ex.: `ISSUED`, `REDEEMED`, `EXPIRED`). |
| `amountCents` | number | Valor do voucher em centavos (valor da emissão, tipo ISSUE). |
| `issuedAt`    | string | Data/hora de emissão (ISO-8601). |
| `expiresAt`   | string | Data/hora de expiração (ISO-8601). |

---
