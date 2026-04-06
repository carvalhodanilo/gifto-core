package com.vp.core.infrastructure.user.model;

import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.user.ScopeType;
import com.vp.core.domain.user.User;
import com.vp.core.domain.user.UserId;
import com.vp.core.domain.user.UserScope;
import com.vp.core.domain.user.UserStatus;
import com.vp.core.domain.valueObjects.Email;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_status", columnList = "status"),
                @Index(name = "idx_users_scope_type", columnList = "scope_type"),
                @Index(name = "idx_users_scope_id", columnList = "scope_id"),
                @Index(name = "idx_users_created_at", columnList = "created_at desc")
        }
)
public class UserJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "keycloak_user_id", unique = true)
    private String keycloakUserId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "scope_type", nullable = false)
    private String scopeType;

    @Column(name = "scope_id")
    private UUID scopeId;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserJpaEntity() {
    }

    public static UserJpaEntity from(final User user) {
        final var e = new UserJpaEntity();
        e.id = UUID.fromString(user.getId().getValue());
        e.email = user.getEmail().getValue();
        e.name = user.getName();
        e.status = user.getStatus().name();
        e.scopeType = user.getScope().getType().name();
        final var sid = user.getScope().getScopeId();
        e.scopeId = sid != null ? UUID.fromString(sid) : null;
        e.keycloakUserId = user.getKeycloakUserId().orElse(null);
        e.createdAt = user.getCreatedAt();
        e.updatedAt = user.getUpdatedAt();
        return e;
    }

    public void applyFrom(final User user) {
        this.email = user.getEmail().getValue();
        this.name = user.getName();
        this.status = user.getStatus().name();
        this.scopeType = user.getScope().getType().name();
        final var sid = user.getScope().getScopeId();
        this.scopeId = sid != null ? UUID.fromString(sid) : null;
        user.getKeycloakUserId().ifPresent(kcId -> this.keycloakUserId = kcId);
        this.updatedAt = user.getUpdatedAt();
    }

    public User toAggregate() {
        final UserScope scope = switch (ScopeType.valueOf(scopeType)) {
            case PLATFORM -> UserScope.platform();
            case TENANT -> UserScope.tenant(TenantId.from(scopeId.toString()));
            case MERCHANT -> UserScope.merchant(MerchantId.from(scopeId.toString()));
        };
        return User.with(
                UserId.from(id.toString()),
                Email.with(email),
                name,
                UserStatus.valueOf(status),
                scope,
                createdAt,
                updatedAt,
                keycloakUserId
        );
    }

    @PrePersist
    void prePersist() {
        final var now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
