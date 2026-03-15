package com.vp.core.domain.user;

import com.vp.core.domain.AggregateRoot;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.validation.ValidationHandler;
import com.vp.core.domain.valueObjects.Email;

import java.time.Instant;

public class User extends AggregateRoot<UserId> {

    private Email email;
    private String name;
    private UserStatus status;

    private final UserScope scope;

    private User(
            final UserId id,
            final Email email,
            final String name,
            final UserStatus status,
            final UserScope scope
    ) {
        super(id);
        this.email = email;
        this.name = name;
        this.status = status;
        this.scope = scope;
    }

    private User(
            final UserId id,
            final Email email,
            final String name,
            final UserStatus status,
            final UserScope scope,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        super(id, createdAt, updatedAt);
        this.email = email;
        this.name = name;
        this.status = status;
        this.scope = scope;
    }

    public static User invitePlatformUser(final Email email, final String name) {
        final var id = UserId.newId();
        return new User(
                id,
                email,
                name,
                UserStatus.INVITED,
                UserScope.platform()
        );
    }

    public static User inviteTenantUser(final Email email, final String name, final TenantId tenantId) {
        final var id = UserId.newId();
        return new User(
                id,
                email,
                name,
                UserStatus.INVITED,
                UserScope.tenant(tenantId)
        );
    }

    public static User inviteMerchantUser(final Email email, final String name, final MerchantId merchantId) {
        final var id = UserId.newId();
        return new User(
                id,
                email,
                name,
                UserStatus.INVITED,
                UserScope.merchant(merchantId)
        );
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
