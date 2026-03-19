# Contrato da API – Settlement (Settlements)

Documentação do contrato dos endpoints de **Settlement** para integração. Base path: **`/v1/settlements`**.

---

## Headers obrigatórios (system_admin)

| Header            | Obrigatório | Descrição |
|------------------|-------------|-----------|
| `Authorization`  | Sim         | `Bearer <access_token>` |
| `tenant`         | Sim         | UUID do tenant (identificação do cliente). |

---

## 1. Executar batch de settlement (run batch)

Calcula os valores devidos a cada merchant para o **período anterior** (semana ISO já fechada) e persiste o batch. O período atual não é usado porque ainda está em aberto. **Não recebe parâmetros da UI**: o período é sempre o anterior. Se já existir batch para esse período, retorna erro.

### Request

- **Método:** `POST`
- **Path:** `/v1/settlements/batch/run`
- **Headers:** `Authorization: Bearer <access_token>`, `tenant` (obrigatório)
- **Body:** nenhum. Não enviar body.

Exemplo: `POST /v1/settlements/batch/run` com `Authorization: Bearer <access_token>` e `tenant: <uuid-do-tenant>`.

### Response – sucesso

- **Status:** `201 Created`
- **Header:** `Location: /v1/settlements/batch/{settlementBatchId}`
- **Body (JSON):**

```json
{
  "settlementBatchId": "550e8400-e29b-41d4-a716-446655440000"
}
```

| Campo              | Tipo   | Descrição |
|--------------------|--------|-----------|
| `settlementBatchId` | string | UUID do batch criado. |

### Erros

| Status | Quando | Body (JSON) |
|--------|--------|-------------|
| `422 Unprocessable Entity` | Já existe batch para o período anterior; ou não há ledger entries para liquidar. | `{ "message": "<descrição do erro>" }` |

Exemplos de mensagens para **422**:
- `"Settlement batch already exists for period 2026-W11."`
- `"No ledger entries to settle"`

---

## 2. Buscar settlement por tenant e período

Retorna o batch de settlement do tenant para o período informado (semana ISO).

### Request

- **Método:** `GET`
- **Path:** `/v1/settlements/batch/{periodKey}`
- **Headers:** `Authorization: Bearer <access_token>`, `tenant` (obrigatório)
- **Path parameter:**

| Nome        | Tipo   | Descrição |
|-------------|--------|-----------|
| `periodKey` | string | Período em formato **ISO week**: `YYYY-Wnn` (ex.: `2026-W11`). |

Exemplo: `GET /v1/settlements/batch/2026-W11` com `Authorization: Bearer <access_token>` e `tenant: <uuid-do-tenant>`.

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "settlementBatchId": "550e8400-e29b-41d4-a716-446655440000",
  "periodKey": "2026-W11",
  "status": "OPEN",
  "closedAt": null,
  "entries": [
    {
      "entryId": "660e8400-e29b-41d4-a716-446655440001",
      "merchantId": "770e8400-e29b-41d4-a716-446655440002",
      "merchantName": "Loja Exemplo Ltda",
      "grossCents": 10000,
      "reversalsCents": 0,
      "feesCents": 500,
      "netCents": 9500,
      "status": "PENDING",
      "paidAt": null,
      "paymentRef": null
    }
  ]
}
```

| Campo (raiz)       | Tipo   | Descrição |
|--------------------|--------|-----------|
| `settlementBatchId` | string | UUID do batch. |
| `periodKey`        | string | Período no formato ISO week (ex.: `2026-W11`). |
| `status`           | string | `OPEN` ou `CLOSED`. |
| `closedAt`         | string (ISO-8601) ou `null` | Data/hora de fechamento do batch, se fechado. |
| `entries`          | array  | Lista de entries (um por merchant). |

Cada objeto em **`entries`**:

| Campo            | Tipo   | Descrição |
|------------------|--------|-----------|
| `entryId`        | string | UUID da entry. |
| `merchantId`     | string | UUID do merchant. |
| `merchantName`   | string ou `null` | Nome do merchant (para identificação na tabela). `null` se o merchant não for encontrado. |
| `grossCents`     | number | Valor bruto (centavos). |
| `reversalsCents` | number | Reversões (centavos). |
| `feesCents`      | number | Taxas (centavos). |
| `netCents`       | number | Líquido (centavos). |
| `status`         | string | `PENDING` ou `PAID`. |
| `paidAt`         | string (ISO-8601) ou `null` | Data/hora do pagamento, se pago. |
| `paymentRef`     | string ou `null` | Referência de pagamento, se pago. |

### Erros

| Status | Quando | Body (JSON) |
|--------|--------|-------------|
| `400 Bad Request` | `periodKey` com formato inválido. | `{ "message": "<descrição do erro>" }` |
| `404 Not Found` | Não existe batch para o tenant e período informados. | `{ "message": "Settlement batch not found for tenant and period <periodKey>" }` |

---

## 3. Marcar entry como paga (mark as paid)

Registra o pagamento de uma entry de settlement (um merchant do batch), informando a referência de pagamento. Se todas as entries do batch ficarem pagas, o batch é fechado automaticamente.

### Request

- **Método:** `PATCH`
- **Path:** `/v1/settlements/batch/{batchId}/entries/{entryId}/paid`
- **Headers:** `Authorization: Bearer <access_token>`, `tenant` (obrigatório), `Content-Type: application/json`
- **Path parameters:**

| Nome       | Tipo   | Descrição |
|------------|--------|-----------|
| `batchId`  | string | UUID do batch de settlement. |
| `entryId`  | string | UUID da entry (uma por merchant no batch). |

- **Body (JSON):**

```json
{
  "paymentRef": "PIX-12345678"
}
```

| Campo        | Tipo   | Obrigatório | Descrição |
|--------------|--------|-------------|-----------|
| `paymentRef` | string | Sim         | Referência do pagamento (ex.: ID do PIX, número do boleto). |

Exemplo: `PATCH /v1/settlements/batch/550e8400-e29b-41d4-a716-446655440000/entries/660e8400-e29b-41d4-a716-446655440001/paid` com `Authorization: Bearer <access_token>`, `tenant: <uuid>` e body acima.

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "settlementBatchId": "550e8400-e29b-41d4-a716-446655440000",
  "entryId": "660e8400-e29b-41d4-a716-446655440001"
}
```

| Campo              | Tipo   | Descrição |
|--------------------|--------|-----------|
| `settlementBatchId` | string | UUID do batch. |
| `entryId`          | string | UUID da entry marcada como paga. |

### Erros

| Status | Quando | Body (JSON) |
|--------|--------|-------------|
| `404 Not Found` | Batch não existe. | `{ "message": "Settlement batch not found: <batchId>" }` |
| `422 Unprocessable Entity` | Entry não existe no batch, ou batch já está fechado. | `{ "message": "<descrição do erro>" }` |

Exemplos de mensagens para **422**:
- `"SettlementEntry not found"`
- `"Batch is closed"`

---

## Formato do período (periodKey) – ISO week

- **Formato:** `YYYY-Wnn`
  - `YYYY`: ano (4 dígitos)
  - `W`: literal
  - `nn`: número da semana (01–53)
- **Exemplos válidos:** `2026-W01`, `2026-W11`, `2025-W53`
- **Semana:** segunda a domingo (ISO 8601).
- **Exemplos inválidos (retornam 400):**
  - Intervalos de datas (ex.: `10032026-17032026`, `11-18`)
  - Semana inexistente (ex.: `2026-W99`)
  - Formato diferente (ex.: só ano, só semana, com espaços/hífens errados)

---

## Formato padrão de erro

Todos os erros documentados retornam um JSON com um único campo:

```json
{
  "message": "Descrição legível do erro."
}
```

---

## Resumo dos endpoints

| Método  | Path | Descrição |
|---------|------|-----------|
| `POST`  | `/v1/settlements/batch/run` | Executa o batch de settlement para o período anterior (semana fechada); `Authorization` + `tenant` no header, sem body. |
| `GET`   | `/v1/settlements/batch/{periodKey}` | Retorna o settlement do tenant para o período (`Authorization` + `tenant` no header). |
| `PATCH` | `/v1/settlements/batch/{batchId}/entries/{entryId}/paid` | Marca a entry como paga (`Authorization` + `tenant` no header, `paymentRef` no body). |
