# Contrato da API – Merchant Redeems

Documentação do contrato do controller `MerchantRedeemStatementController`. Base path: **`/v1/merchants`**.

---
## Autenticação JWT obrigatória

Os endpoints são protegidos por JWT emitido pelo Keycloak. O client deve enviar:

| Header        | Obrigatório | Descrição |
|---------------|-------------|-----------|
| `Authorization` | Sim | `Bearer <access_token>` |

O backend valida o escopo (tenant/merchant) a partir das claims `tenant_id` e `merchant_id` no token.

---
## 1. Listar redeem statements por merchant e período

Lista redeem entries de vouchers (ledger entries) para um `merchantId`, filtrando por intervalo de datas.

### Request

- **Método:** `GET`
- **Path:** `/v1/merchants/{merchantId}/redeems`

### Segurança
- Roles: `tenant_admin`, `tenant_operator`, `merchant_admin`, `merchant_operator`
- Regra de escopo:
  - Para `merchant_admin`/`merchant_operator`: `merchantId` precisa ser o mesmo do token (`merchant_id`).
  - Para `tenant_admin`/`tenant_operator`: o `merchantId` precisa pertencer ao tenant do token (`tenant_id`).

### Path parameter

| Nome | Tipo | Descrição |
|------|------|-----------|
| `merchantId` | string | UUID do merchant |

### Query parameters

| Nome | Tipo | Obrigatório | Default | Descrição |
|------|------|--------------|---------|-----------|
| `from` | string (ISO-8601) | Não | (depende do momento atual) | Início do intervalo |
| `to` | string (ISO-8601) | Não | agora | Fim do intervalo |
| `status` | string | Não | `ALL` | `ALL`, `PENDING`, `PAID` |
| `page` | number | Não | `0` | Página (zero-based) |
| `size` | number | Não | `20` | Tamanho da página |

Observação de padrão:
- Se `to` não for enviado, usa `agora`.
- Se `from` não for enviado, usa `to - 7 dias`.

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):** `ListMerchantRedeemsByPeriodOutput`

```json
{
  "period": {
    "from": "2026-01-01T00:00:00Z",
    "to": "2026-01-08T00:00:00Z"
  },
  "summary": {
    "grossRedeemsCents": 10000,
    "reversalsCents": 0,
    "netSubtotalCents": 9500
  },
  "pagination": {
    "currentPage": 0,
    "perPage": 20,
    "total": 42,
    "items": [
      {
        "ledgerEntryId": "660e8400-e29b-41d4-a716-446655440001",
        "voucherId": "770e8400-e29b-41d4-a716-446655440002",
        "displayCode": "ABC123",
        "amountCents": 5000,
        "createdAt": "2026-01-02T10:00:00Z",
        "settlement": {
          "status": "PENDING",
          "settlementEntryId": "880e8400-e29b-41d4-a716-446655440003",
          "settlementBatchId": "990e8400-e29b-41d4-a716-446655440004",
          "paidAt": null
        }
      }
    ]
  }
}
```

---
## Formato padrão de erro

Os erros retornam um JSON com um único campo:

```json
{
  "message": "Descrição legível do erro."
}
```

