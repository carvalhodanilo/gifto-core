# Prompt para Cursor — Front-end React + Vite (integração com backend Gifto em MVP)

Copie o bloco "INSTRUÇÕES PARA O AGENTE" abaixo e cole em um novo chat do Cursor no repositório do seu **front-end** (React + Vite).

---

## Contexto fixo (não contradizer)

- O backend Spring Boot já está no ar atrás de Nginx na AWS Lightsail.
- Sem domínio neste momento: tudo roda em `http://44.213.123.87/`.
- Sem HTTPS neste MVP: tráfego HTTP puro. Depois vamos adicionar TLS (Nginx + Let’s Encrypt) e então revisitar configs.
- Proxy (Nginx):
  - `/api/` -> backend Spring (Nginx remove o prefixo `/api` ao encaminhar; na API as rotas continuam como `/v1/...`, `/tenants`, etc.).
  - `/auth/` -> Keycloak (`KC_HTTP_RELATIVE_PATH=/auth`).
- Realm Keycloak: `gifto`.
- Client web (login no browser) no Keycloak: `voucher-platform-admin-web` ou `voucher-platform-sales-web` (publicClient, PKCE).
- Client das roles no token (para o backend) no Keycloak: `voucher-platform-api`.

---

## INSTRUÇÕES PARA O AGENTE (cole no Cursor)

Você está no repositório de um app **React + Vite**. O backend já está deployado e funcional. Sua tarefa é implementar/configurar o front para casar com esse backend e Keycloak, sem reinventar o contrato.

### URLs base (ambiente MVP atual)

| Uso | URL |
|-----|-----|
| API (via proxy) | `http://44.213.123.87/api` |
| Keycloak (base) | `http://44.213.123.87/auth` |
| Issuer OIDC | `http://44.213.123.87/auth/realms/gifto` |

Exemplos:
- Health da API: `GET http://44.213.123.87/api/actuator/health`
- OpenID discovery: `GET http://44.213.123.87/auth/realms/gifto/.well-known/openid-configuration`

### Variáveis de ambiente Vite (alinhadas ao seu snippet)


```env
# Obrigatorio: URL (origem) onde o Vite roda no browser
# Exemplo dev local:
# VITE_APP_URL=http://localhost:5174
# Exemplo dev acessando pelo IP na rede:
# VITE_APP_URL=http://44.213.123.87:5174
VITE_APP_URL=http://localhost:5174

# Obrigatorio: base HTTP publica da API (prefixo /api do Nginx)
VITE_API_BASE_URL=http://44.213.123.87/api

# Obrigatorio: base do Keycloak para o keycloak-js
VITE_KEYCLOAK_URL=http://44.213.123.87/auth
VITE_KEYCLOAK_REALM=gifto

# Obrigatorio: client web (um por vez)
VITE_KEYCLOAK_CLIENT_ID=voucher-platform-sales-web
# ou: VITE_KEYCLOAK_CLIENT_ID=voucher-platform-admin-web
```

Em código, use somente `import.meta.env.VITE_*` (nunca secrets no front).

### Checklist rapido (informacoes que voce precisa reunir)

1. IP publico da instancia e a porta/origem onde o front vai rodar (ex.: `http://44.213.123.87:5174`).
2. URL publica exata da API que o browser chama (base com prefixo `/api`, ex.: `http://44.213.123.87/api`).
3. Keycloak em PROD: `VITE_KEYCLOAK_URL`, `VITE_KEYCLOAK_REALM`, `VITE_KEYCLOAK_CLIENT_ID`.
4. No Keycloak (client web): `Valid redirect URIs` e `Web origins` com a origem do front (IP + porta) e sem dominio.
5. CORS: se o front rodar em outra origem (porta), alinhar backend para permitir aquela origem.

### Keycloak no front (SPA)

- Usar fluxo Authorization Code + PKCE.
- Biblioteca recomendada: `keycloak-js`.
- Configurar Keycloak com:
  - `url`: `VITE_KEYCLOAK_URL`
  - `realm`: `VITE_KEYCLOAK_REALM`
  - `clientId`: `VITE_KEYCLOAK_CLIENT_ID`

Seu snippet usa `keycloakConfig` com `appUrl: requiredEnv('VITE_APP_URL')`, entao `VITE_APP_URL` e obrigatorio.

### Redirect URIs e Web origins no Keycloak (obrigatorio)

No Admin Console do Keycloak, no client web (`voucher-platform-admin-web` ou `voucher-platform-sales-web`):

1. `Valid redirect URIs`: incluir `VITE_APP_URL/*` (ex.: `http://44.213.123.87:5174/*`).
2. `Web origins`: incluir somente `VITE_APP_URL` sem `/*` (ex.: `http://44.213.123.87:5174`).

Sem isso, o login OIDC falha com redirect invalido.

### Chamadas a API

- Requisicoes autenticadas: `Authorization: Bearer <access_token>`.
- Base URL das chamadas REST: `VITE_API_BASE_URL`.
- Se o backend tem rotas `@RequestMapping("/v1/vouchers")`, a URL publica fica:
  - `VITE_API_BASE_URL/v1/vouchers/...`

Se voce implementar `getApiBaseUrl()` e `apiUrl(path)` no mesmo estilo do seu snippet, garanta que nao dobre barras.

### Claims / roles

- Roles de negocio do token: `resource_access['voucher-platform-api'].roles`.
- Claims custom: `tenant_id`, `merchant_id` quando aplicavel.

### O que nao fazer

- Nao usar `voucher-platform-api` como client web do front.
- Nao assumir HTTPS ate migrarmos; depois trocar URLs para `https://`.

---

## TODOS (ordem sugerida de implementacao no Cursor)

1. Criar/ajustar `.env.example` com `VITE_APP_URL`, `VITE_API_BASE_URL`, `VITE_KEYCLOAK_URL`, `VITE_KEYCLOAK_REALM`, `VITE_KEYCLOAK_CLIENT_ID`.
2. Implementar `requiredEnv()` e `keycloakConfig` exatamente como no seu snippet (incluindo `appUrl: requiredEnv('VITE_APP_URL')`).
3. Criar o provider de autenticacao no React (Keycloak + rotas protegidas).
4. Implementar helpers `getApiBaseUrl()` e `apiUrl(path)` usando `VITE_API_BASE_URL`.
5. Implementar client HTTP (fetch/axios) que injeta `Authorization: Bearer <token>`.
6. Proteger as rotas front com base em roles/claims vindos do token.

---

## Entregaveis esperados

1. `README.md` no front com envs e como apontar para o backend remoto.
2. Provider/Auth no React com Keycloak.
3. Client HTTP que injeta Bearer token automaticamente.
4. `.env.example` alinhado ao checklist.

---

## Referencia rapida — backend (repo `core`)

- Deploy: `deploy/lightsail/README.md`.
- Realm / clients: `infra/keycloak/README.md`.
