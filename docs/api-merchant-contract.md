# Contrato da API – Merchants

Documentação do contrato dos endpoints de **Merchant** para integração. Base path: **`/merchants`**.

---

## Autenticação JWT obrigatória

Os endpoints são protegidos por JWT emitido pelo Keycloak. O client deve enviar:

| Header | Obrigatório | Descrição |
|---------|-------------|-----------|
| `Authorization` | Sim | `Bearer <access_token>` |

O backend resolve `tenantId`/`merchantId` a partir das claims do token (`tenant_id` e `merchant_id`).

---
<!--
## 1. Listar merchants por tenant (paginado) (duplicado)

Retorna uma lista paginada de merchants do tenant, com dados básicos para preencher uma tabela (ID, Nome Fantasia, Status). Permite filtrar por termos de busca e por status.

### Request

- **Método:** `GET`
- **Path:** `/merchants`
- **Headers:** `Authorization: Bearer <access_token>`
- **Query parameters:**

| Nome     | Tipo   | Obrigatório | Default | Descrição |
|----------|--------|-------------|---------|-----------|
| `page`   | number | Não         | `0`     | Página (zero-based). |
| `perPage`| number | Não         | `10`    | Quantidade por página. |
| `terms`  | string | Não         | —       | Busca por nome ou nome fantasia (case insensitive, parcial). |
| `status` | string | Não         | —       | Filtrar por status: `ACTIVE` ou `SUSPENDED`. |


| Header   | Obrigatório | Descrição                          |
|----------|-------------|------------------------------------|
| `Authorization` | Sim | `Bearer <access_token>` |

---

-->
## 1. Listar merchants por tenant (paginado)

Retorna uma lista paginada de merchants do tenant, com dados básicos para preencher uma tabela (ID, Nome Fantasia, Status). Permite filtrar por termos de busca e por status.

### Request

- **Método:** `GET`
- **Path:** `/merchants`
- **Headers:** `Authorization: Bearer <access_token>`
- **Query parameters:**

| Nome     | Tipo   | Obrigatório | Default | Descrição |
|----------|--------|-------------|---------|-----------|
| `page`   | number | Não         | `0`     | Página (zero-based). |
| `perPage`| number | Não         | `10`    | Quantidade por página. |
| `terms`  | string | Não         | —       | Busca por nome ou nome fantasia (case insensitive, parcial). |
| `status` | string | Não         | —       | Filtrar por status: `ACTIVE` ou `SUSPENDED`. |

Exemplo: `GET /merchants?page=0&perPage=10&terms=loja&status=ACTIVE` com `Authorization: Bearer <access_token>` (onde a claim `tenant_id` define o tenant).

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "currentPage": 0,
  "perPage": 10,
  "total": 25,
  "items": [
    {
      "merchantId": "550e8400-e29b-41d4-a716-446655440000",
      "fantasyName": "Loja Exemplo",
      "status": "ACTIVE"
    }
  ]
}
```

| Campo (raiz)  | Tipo   | Descrição |
|---------------|--------|-----------|
| `currentPage` | number | Página atual (zero-based). |
| `perPage`     | number | Tamanho da página. |
| `total`       | number | Total de registros que atendem aos filtros. |
| `items`       | array  | Lista de merchants. |

Cada objeto em **`items`**:

| Campo        | Tipo   | Descrição |
|--------------|--------|-----------|
| `merchantId` | string | UUID do merchant. |
| `fantasyName`| string ou `null` | Nome fantasia. |
| `status`     | string | `ACTIVE` ou `SUSPENDED`. |

---

## 2. Obter detalhes do merchant

Retorna os detalhes completos do merchant (sem dados bancários): identificação, contato, endereço, status e redes ativas.

### Request

- **Método:** `GET`
- **Path:** `/merchants/{merchantId}`
- **Headers:** `Authorization: Bearer <access_token>`
- **Path parameter:**

| Nome         | Tipo   | Descrição |
|--------------|--------|-----------|
| `merchantId` | string | UUID do merchant. |

Exemplo: `GET /merchants/550e8400-e29b-41d4-a716-446655440000` com `Authorization: Bearer <access_token>` (claim `merchant_id` define o merchant).

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "merchantId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Loja Exemplo Ltda",
  "fantasyName": "Loja Exemplo",
  "document": "12345678000199",
  "email": "contato@lojaexemplo.com.br",
  "phone1": "11999990000",
  "phone2": null,
  "url": "https://lojaexemplo.com.br",
  "status": "ACTIVE",
  "location": {
    "street": "Rua das Flores",
    "number": "100",
    "neighborhood": "Centro",
    "complement": "Sala 2",
    "city": "São Paulo",
    "state": "SP",
    "country": "Brasil",
    "postalCode": "01310100"
  },
  "activeNetworkIds": ["660e8400-e29b-41d4-a716-446655440001"],
  "createdAt": "2026-01-15T10:00:00Z",
  "updatedAt": "2026-03-10T14:30:00Z"
}
```

| Campo (raiz)       | Tipo   | Descrição |
|--------------------|--------|-----------|
| `merchantId`       | string | UUID do merchant. |
| `name`             | string | Razão social. |
| `fantasyName`      | string ou `null` | Nome fantasia. |
| `document`         | string | Documento (CPF/CNPJ). |
| `email`            | string | E-mail. |
| `phone1`           | string ou `null` | Telefone 1. |
| `phone2`           | string ou `null` | Telefone 2. |
| `url`              | string | URL do merchant. |
| `status`           | string | `ACTIVE` ou `SUSPENDED`. |
| `location`         | object ou `null` | Endereço; `null` se não informado. |
| `activeNetworkIds` | array  | Lista de UUIDs das redes em que o merchant está ativo. |
| `createdAt`        | string (ISO-8601) | Data/hora de criação. |
| `updatedAt`        | string (ISO-8601) | Data/hora da última atualização. |

Campos do objeto **`location`** (quando não for `null`):

| Campo         | Tipo   | Descrição |
|---------------|--------|-----------|
| `street`      | string ou `null` | Logradouro. |
| `number`      | string ou `null` | Número. |
| `neighborhood`| string ou `null` | Bairro. |
| `complement`  | string ou `null` | Complemento. |
| `city`        | string ou `null` | Cidade. |
| `state`       | string ou `null` | Estado (UF). |
| `country`     | string ou `null` | País. |
| `postalCode`  | string ou `null` | CEP. |

### Erros

| Status | Quando | Body (JSON) |
|--------|--------|-------------|
| `404 Not Found` | Merchant não existe ou não pertence ao tenant do token. | `{ "message": "<descrição do erro>" }` |

---

## 3. GET – Obter dados bancários do merchant

Retorna **apenas** os dados da conta bancária do merchant. Endpoint separado por segurança e menor exposição de dados sensíveis.

### Request

- **Método:** `GET`
- **Path:** `/merchants/{merchantId}/bank-account`
- **Headers:** `Authorization: Bearer <access_token>`
- **Path parameter:**

| Nome         | Tipo   | Descrição |
|--------------|--------|-----------|
| `merchantId` | string | UUID do merchant. |

Exemplo: `GET /merchants/550e8400-e29b-41d4-a716-446655440000/bank-account` com `Authorization: Bearer <access_token>`.

### Response – sucesso

- **Status:** `200 OK` quando o merchant possui conta bancária cadastrada.
- **Body (JSON):**

```json
{
  "merchantId": "550e8400-e29b-41d4-a716-446655440000",
  "bankCode": "001",
  "bankName": "Banco do Brasil",
  "branch": "1234",
  "accountNumber": "12345",
  "accountDigit": "6",
  "accountType": "CHECKING",
  "holderName": "Loja Exemplo Ltda",
  "holderDocument": "12345678000199",
  "pixKeyType": "CNPJ",
  "pixKeyValue": "12345678000199"
}
```

| Campo            | Tipo   | Descrição |
|------------------|--------|-----------|
| `merchantId`     | string | UUID do merchant. |
| `bankCode`       | string ou `null` | Código do banco. |
| `bankName`       | string ou `null` | Nome do banco. |
| `branch`         | string ou `null` | Agência. |
| `accountNumber`  | string ou `null` | Número da conta. |
| `accountDigit`   | string ou `null` | Dígito da conta. |
| `accountType`    | string ou `null` | Tipo: `CHECKING`, `SAVINGS` ou `PAYMENT`. |
| `holderName`     | string ou `null` | Nome do titular. |
| `holderDocument` | string ou `null` | CPF/CNPJ do titular. |
| `pixKeyType`     | string ou `null` | Tipo da chave PIX: `CPF`, `CNPJ`, `EMAIL`, `PHONE`, `RANDOM`. |
| `pixKeyValue`    | string ou `null` | Valor da chave PIX. |

- **Status:** `204 No Content` quando o merchant existe e pertence ao tenant, mas **não possui conta bancária cadastrada**. Sem body.

### Erros

| Status | Quando | Body (JSON) |
|--------|--------|-------------|
| `404 Not Found` | Merchant não existe ou não pertence ao tenant do token. | `{ "message": "<descrição do erro>" }` |

---

## 4. PUT – Atualizar dados bancários do merchant

Atualiza **apenas** os dados da conta bancária do merchant. Endpoint separado por segurança.

### Request

- **Método:** `PUT`
- **Path:** `/merchants/{merchantId}/bank-account`
- **Headers:** `Authorization: Bearer <access_token>`, `Content-Type: application/json`
- **Path parameter:**

| Nome         | Tipo   | Descrição |
|--------------|--------|-----------|
| `merchantId` | string | UUID do merchant. |

- **Body (JSON):**

```json
{
  "bankCode": "001",
  "bankName": "Banco do Brasil",
  "branch": "1234",
  "accountNumber": "12345",
  "accountDigit": "6",
  "accountType": "CHECKING",
  "holderName": "Loja Exemplo Ltda",
  "holderDocument": "12345678000199",
  "pixKeyType": "CNPJ",
  "pixKeyValue": "12345678000199"
}
```

| Campo            | Tipo   | Obrigatório | Descrição |
|------------------|--------|-------------|-----------|
| `bankCode`       | string | Não         | Código do banco. |
| `bankName`       | string | Não         | Nome do banco. |
| `branch`         | string | Não         | Agência. |
| `accountNumber`  | string | Não         | Número da conta. |
| `accountDigit`   | string | Não         | Dígito da conta. |
| `accountType`    | string | Sim         | Tipo: `CHECKING`, `SAVINGS` ou `PAYMENT`. |
| `holderName`     | string | Não         | Nome do titular. |
| `holderDocument` | string | Não         | CPF/CNPJ do titular. |
| `pixKeyType`     | string | Não         | Tipo da chave PIX: `CPF`, `CNPJ`, `EMAIL`, `PHONE`, `RANDOM`. |
| `pixKeyValue`    | string | Não         | Valor da chave PIX. |

Exemplo: `PUT /merchants/550e8400-e29b-41d4-a716-446655440000/bank-account` com `Authorization: Bearer <access_token>` e body acima.

### Response – sucesso

- **Status:** `200 OK`
- **Body (JSON):**

```json
{
  "merchantId": "550e8400-e29b-41d4-a716-446655440000"
}
```

| Campo        | Tipo   | Descrição |
|--------------|--------|-----------|
| `merchantId` | string | UUID do merchant. |

### Erros

| Status | Quando | Body (JSON) |
|--------|--------|-------------|
| `404 Not Found` | Merchant não existe ou não pertence ao tenant do token. | `{ "message": "<descrição do erro>" }` |

---

## 5. POST – Ativar merchant

Altera o status do merchant para `ACTIVE`. Se já estiver ativo, a operação é idempotente (não gera erro).

### Request

- **Método:** `POST`
- **Path:** `/merchants/{merchantId}/activate`
- **Headers:** `Authorization: Bearer <access_token>`
- **Path parameter:**

| Nome         | Tipo   | Descrição |
|--------------|--------|-----------|
| `merchantId` | string | UUID do merchant. |

Exemplo: `POST /merchants/550e8400-e29b-41d4-a716-446655440000/activate` com `Authorization: Bearer <access_token>`.

### Response – sucesso

- **Status:** `204 No Content`
- **Body:** nenhum.

### Erros

| Status | Quando | Body (JSON) |
|--------|--------|-------------|
| `404 Not Found` | Merchant não existe ou não pertence ao tenant do token. | `{ "message": "<descrição do erro>" }` |

---

## 6. POST – Suspender merchant

Altera o status do merchant para `SUSPENDED`. Se já estiver suspenso, a operação é idempotente (não gera erro).

### Request

- **Método:** `POST`
- **Path:** `/merchants/{merchantId}/suspend`
- **Headers:** `Authorization: Bearer <access_token>`
- **Path parameter:**

| Nome         | Tipo   | Descrição |
|--------------|--------|-----------|
| `merchantId` | string | UUID do merchant. |

Exemplo: `POST /merchants/550e8400-e29b-41d4-a716-446655440000/suspend` com `Authorization: Bearer <access_token>`.

### Response – sucesso

- **Status:** `204 No Content`
- **Body:** nenhum.

### Erros

| Status | Quando | Body (JSON) |
|--------|--------|-------------|
| `404 Not Found` | Merchant não existe ou não pertence ao tenant do token. | `{ "message": "<descrição do erro>" }` |

---

## Resumo dos endpoints documentados

| Método | Path | Descrição |
|--------|------|-----------|
| `GET`  | `/merchants` | Lista paginada de merchants do tenant (tabela: ID, Nome Fantasia, Status). Query: `page`, `perPage`, `terms`, `status`. |
| `GET`  | `/merchants/{merchantId}` | Detalhes do merchant (sem dados bancários): contato, endereço, redes, etc. |
| `GET`  | `/merchants/{merchantId}/bank-account` | Obter dados bancários do merchant. |
| `PUT`  | `/merchants/{merchantId}/bank-account` | Atualizar dados bancários do merchant (body: conta, titular, PIX). |
| `POST` | `/merchants/{merchantId}/activate` | Ativar merchant (status → ACTIVE). |
| `POST` | `/merchants/{merchantId}/suspend` | Suspender merchant (status → SUSPENDED). |

---

## Formato padrão de erro

Os erros retornam um JSON com um único campo:

```json
{
  "message": "Descrição legível do erro."
}
```
