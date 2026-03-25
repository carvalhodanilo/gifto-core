# Deploy MVP — AWS Lightsail (Docker Compose + Nginx)

Stack mínima para validar o **backend** em nuvem: **Nginx** (porta 80) + **Spring Boot** + **Keycloak** + **dois Postgres** (app e Keycloak), sem front-end, sem Kubernetes, sem automação de deploy.

### Lembrete: Keycloak e tráfego **sem SSL** (MVP)

Neste deploy **não há HTTPS** (nem no Nginx nem entre browser e servidor). O realm importado (`realm-gifto.json`) vem com `sslRequired: external`; em ambiente só HTTP é comum precisar de **SSL required = None** no realm **`gifto`** e, para o **Admin Console**, também no realm **`master`** — caso contrário aparece *“HTTPS required”*.

**Isso é aceitável só para MVP / validação.** O próximo passo natural é **domínio + TLS no Nginx** (ex.: Let’s Encrypt) e então **voltar a exigir SSL** nos realms Keycloak (`external` ou `all`, conforme política) e atualizar todas as URLs para `https://` (backend, Keycloak, variáveis do front).

Integração do front (React + Vite) com esta stack: ver [`FRONTEND_VITE_CURSOR_PROMPT.md`](FRONTEND_VITE_CURSOR_PROMPT.md).

## O que foi gerado

| Arquivo / pasta | Descrição |
|-----------------|-----------|
| [`docker-compose.yml`](docker-compose.yml) | Serviços: `reverse-proxy`, `backend`, `postgres`, `keycloak`, `keycloak-postgres` |
| [`nginx/nginx.conf`](nginx/nginx.conf) | Roteamento: `/api/` → backend (remove prefixo), `/auth/` → Keycloak |
| [`.env.lightsail.example`](.env.lightsail.example) | Modelo de variáveis de ambiente |
| [`../../Dockerfile`](../../Dockerfile) | Imagem do backend (Maven multi-stage, Java 21) |
| [`../../src/main/resources/application-lightsail.yaml`](../../src/main/resources/application-lightsail.yaml) | Perfil Spring: datasource por env, `issuer-uri` + `jwk-set-uri` interno, CORS por `PUBLIC_IP` |
| [`FRONTEND_VITE_CURSOR_PROMPT.md`](FRONTEND_VITE_CURSOR_PROMPT.md) | Prompt para usar no Cursor no app **React + Vite** (alinhado a este backend) |

O realm Keycloak continua em [`../../infra/keycloak/realm`](../../infra/keycloak/realm) (mesmo JSON do ambiente local).

## URLs externas (sem domínio)

Com o IP público da VM `SEU_IP`:

| Uso | URL |
|-----|-----|
| Health da API | `http://SEU_IP/api/actuator/health` |
| OpenID (well-known) | `http://SEU_IP/auth/realms/gifto/.well-known/openid-configuration` |
| Admin Keycloak | `http://SEU_IP/auth/admin/` |

O backend Spring expõe rotas na raiz (ex.: `/v1/vouchers`); pelo proxy elas ficam em **`/api/...`** (o Nginx remove o prefixo `/api`).

## Pré-requisitos na Lightsail

1. Instância **Linux** (Ubuntu 22.04 LTS ou similar), com Docker Engine e plugin **Docker Compose v2**.
2. **Firewall / rede**: liberar **TCP 22** (SSH) e **TCP 80** (HTTP). Não é necessário expor 5432 nem 8080 no host — só o Nginx publica a 80.

### Instalar Docker (Ubuntu — resumo)

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo usermod -aG docker "$USER"
```

Saia e entre de novo na sessão SSH para o grupo `docker` valer.

## Deploy manual

1. **Copiar o repositório** (ou ao menos: raiz do módulo `core` com `src/`, `pom.xml`, `Dockerfile`, `infra/keycloak/realm/`, `deploy/lightsail/`).

2. Na máquina, na **raiz do projeto `core`**:

   ```bash
   cp deploy/lightsail/.env.lightsail.example deploy/lightsail/.env
   # Edite deploy/lightsail/.env: PUBLIC_IP, senhas, KEYCLOAK_ISSUER_URI, VOUCHER_TOKEN_SECRET
   ```

3. **Subir**:

   ```bash
   docker compose --env-file deploy/lightsail/.env -f deploy/lightsail/docker-compose.yml up -d --build
   ```

   Alternativa (se você já estiver em `deploy/lightsail/`):

   ```bash
   cp .env.lightsail.example .env
   docker compose --env-file .env -f docker-compose.yml up -d --build
   ```

4. **Logs** (se necessário):

   ```bash
   docker compose --env-file deploy/lightsail/.env -f deploy/lightsail/docker-compose.yml logs -f backend keycloak reverse-proxy
   ```

## Variáveis obrigatórias no `.env`

- **`PUBLIC_IP`**: IP público estático da Lightsail (sem `http://`).
- **`POSTGRES_PASSWORD`**, **`KC_DB_PASSWORD`**, **`KEYCLOAK_ADMIN_PASSWORD`**: senhas fortes.
- **`KEYCLOAK_ISSUER_URI`**: deve ser `http://SEU_IP/auth/realms/gifto` (igual ao `iss` dos JWT).
- **`VOUCHER_TOKEN_SECRET`**: segredo longo para HMAC dos vouchers.

O compose define `SPRING_PROFILES_ACTIVE=lightsail` no container do backend.

## Verificação rápida

```bash
IP=SEU_IP   # substitua

curl -sS "http://${IP}/api/actuator/health"
curl -sS "http://${IP}/auth/realms/gifto/.well-known/openid-configuration" | head
```

Obter token (Direct Access Grants — mesmo modelo do README do Keycloak local; **somente para testes / MVP**):

```bash
curl -s -X POST "http://${IP}/auth/realms/gifto/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=voucher-platform-admin-web" \
  -d "username=tenant.admin@local.test" \
  -d "password=Local123!"
```

Chamada autenticada na API:

```bash
TOKEN="..." # access_token do passo anterior
curl -sS -H "Authorization: Bearer ${TOKEN}" "http://${IP}/api/v1/vouchers/..." 
```

(Ajuste o path conforme o endpoint desejado.)

## Keycloak neste cenário (HTTP + IP + path `/auth`)

### Por que `issuer` e JWKS separados

O backend valida o JWT com:

- **`issuer-uri`** = URL **pública** (`http://SEU_IP/auth/realms/gifto`), alinhada ao claim `iss`.
- **`jwk-set-uri`** = URL **interna** na rede Docker (`http://keycloak:8080/auth/.../certs`), para não depender de *hairpin* (VM acessar o próprio IP público).

### Ajustes recomendados após o primeiro boot

1. **SSL no realm (HTTP puro)**  
   O import usa `sslRequired: external`. Para admin e fluxos via **HTTP**, em **Realm settings → General → SSL required** defina **None** (aceitável só para MVP).

2. **Clients web (`redirectUris` / `Web origins`)**  
   O JSON importado aponta para `localhost`. Para testar login no browser pelo IP, inclua manualmente algo como `http://SEU_IP/*` e *Web origins* `http://SEU_IP` nos clients `voucher-platform-admin-web` e `voucher-platform-sales-web`.

3. **Keycloak atrás do proxy**  
   O compose define `KC_HTTP_RELATIVE_PATH=/auth`, `KC_PROXY_HEADERS=xforwarded`, `KC_HOSTNAME` = `PUBLIC_IP`, `KC_HOSTNAME_STRICT=false`, `KC_HOSTNAME_STRICT_HTTPS=false`.

### Quando tiver domínio + HTTPS (reativar SSL)

- Apontar DNS para a VM; no Nginx terminar TLS (ex.: Let’s Encrypt).
- Atualizar **`PUBLIC_IP`** / hostname nas envs do Keycloak e **`KEYCLOAK_ISSUER_URI`** para `https://seu-dominio/auth/realms/gifto`.
- Ajustar **`KEYCLOAK_JWK_SET_URI`** se mudar o path interno (normalmente permanece o mesmo serviço Docker).
- Atualizar CORS no perfil `lightsail` (origins ou padrões com `https://`).
- **Reativar SSL** nos realms Keycloak (`external` ou `all`): alinhar com `realm-gifto.json` ou ajustar no Admin Console; remover a exceção “HTTP só” do MVP.
- Atualizar variáveis do front (`VITE_*`) e URLs de redirect no Keycloak para `https://`.

## Limitações aceitáveis do MVP

- Tráfego **HTTP sem criptografia**; credenciais trafegam em claro na Internet.
- **Um único host**; sem HA nem backups automatizados neste guia.
- Keycloak em modo **`start-dev`** (igual ao compose local), não otimizado para produção.
- Segredos em arquivo **`.env`** na VM (sem Secrets Manager).
- Primeiro import do realm: alterações no JSON exigem procedimento de reimport (ver [`../../infra/keycloak/README.md`](../../infra/keycloak/README.md)).

## Próxima evolução (fora do escopo atual)

- **Automação de deploy**: por exemplo **GitHub Actions** (build da imagem ou artefato + SSH na Lightsail + `docker compose pull/up`).
- Domínio, **HTTPS**, endurecimento de Keycloak (`start` otimizado), rotação de segredos e backups do Postgres.

---

**Dica:** começar com **IP público** e variáveis centralizadas no `.env` reduz retrabalho na hora de plugar domínio e TLS — em geral é **reconfiguração + Nginx**, não reescrita do backend.