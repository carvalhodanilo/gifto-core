-- Identificador interno do utilizador no Keycloak (Admin API); opcional para linhas legadas/seed.
-- Em PostgreSQL, UNIQUE permite várias linhas com NULL.
alter table users
    add column keycloak_user_id text null;

alter table users
    add constraint uq_users_keycloak_user_id unique (keycloak_user_id);
