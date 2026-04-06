package com.vp.core.domain.user;

import com.vp.core.domain.AggregateRoot;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.validation.ValidationHandler;
import com.vp.core.domain.valueObjects.Email;

import java.time.Instant;
import java.util.Optional;

public class User extends AggregateRoot<UserId> {

    private Email email;
    private String name;
    private UserStatus status;

    private final UserScope scope;

    /** Id interno no Keycloak; preenchido após provisionamento (pode ser null antes ou em dados legados). */
    private String keycloakUserId;

    private User(
            final UserId id,
            final Email email,
            final String name,
            final UserStatus status,
            final UserScope scope,
            final String keycloakUserId
    ) {
        super(id);
        this.email = email;
        this.name = name;
        this.status = status;
        this.scope = scope;
        this.keycloakUserId = keycloakUserId;
    }

    private User(
            final UserId id,
            final Email email,
            final String name,
            final UserStatus status,
            final UserScope scope,
            final Instant createdAt,
            final Instant updatedAt,
            final String keycloakUserId
    ) {
        super(id, createdAt, updatedAt);
        this.email = email;
        this.name = name;
        this.status = status;
        this.scope = scope;
        this.keycloakUserId = keycloakUserId;
    }

    public static User invitePlatformUser(final Email email, final String name) {
        final var id = UserId.newId();
        return new User(
                id,
                email,
                name,
                UserStatus.INVITED,
                UserScope.platform(),
                null
        );
    }

    public static User inviteTenantUser(final Email email, final String name, final TenantId tenantId) {
        final var id = UserId.newId();
        return new User(
                id,
                email,
                name,
                UserStatus.INVITED,
                UserScope.tenant(tenantId),
                null
        );
    }

    public static User inviteMerchantUser(final Email email, final String name, final MerchantId merchantId) {
        final var id = UserId.newId();
        return new User(
                id,
                email,
                name,
                UserStatus.INVITED,
                UserScope.merchant(merchantId),
                null
        );
    }

    /** Reconstituição a partir da persistência (infra). */
    public static User with(
            final UserId id,
            final Email email,
            final String name,
            final UserStatus status,
            final UserScope scope,
            final Instant createdAt,
            final Instant updatedAt,
            final String keycloakUserId
    ) {
        return new User(id, email, name, status, scope, createdAt, updatedAt, keycloakUserId);
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        touch();
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
        touch();
    }

    public void rename(final String newName) {
        this.name = newName;
        touch();
    }

    public void changeEmail(final Email newEmail) {
        this.email = newEmail;
        touch();
    }

    public void assignKeycloakUserId(final String keycloakUserId) {
        this.keycloakUserId = keycloakUserId;
        touch();
    }

    public Optional<String> getKeycloakUserId() {
        return Optional.ofNullable(keycloakUserId);
    }

    public Email getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public UserStatus getStatus() {
        return status;
    }

    public UserScope getScope() {
        return scope;
    }

    @Override
    public void validate(final ValidationHandler handler) {
    }
}
