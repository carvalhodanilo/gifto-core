package com.vp.core.infrastructure.network.model;

import com.vp.core.domain.network.Network;
import com.vp.core.domain.network.NetworkId;
import com.vp.core.domain.network.NetworkStatus;
import com.vp.core.domain.network.NetworkType;
import com.vp.core.domain.network.TenantNetworkMembership;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "networks",
        indexes = {
                @Index(name = "idx_networks_status", columnList = "status"),
                @Index(name = "idx_networks_type", columnList = "type")
        }
)
public class NetworkJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "status", nullable = false)
    private String status;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "network", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private Set<TenantNetworkJpaEntity> memberships = new HashSet<>();

    protected NetworkJpaEntity() {
    }

    public static NetworkJpaEntity from(final Network network) {
        final var e = new NetworkJpaEntity();

        e.id = UUID.fromString(network.getId().getValue());
        e.name = network.name();
        e.type = network.type().name();
        e.status = network.status().name();

        e.createdAt = network.getCreatedAt();
        e.updatedAt = network.getUpdatedAt();

        e.memberships = network.memberships().stream()
                .map(m -> TenantNetworkJpaEntity.from(e, m))
                .collect(Collectors.toSet());

        return e;
    }

    public Network toAggregate() {
        return Network.with(
                NetworkId.from(String.valueOf(id)),
                name,
                NetworkType.valueOf(type),
                NetworkStatus.valueOf(status),
                memberships.stream()
                        .map(TenantNetworkJpaEntity::toDomain)
                        .collect(Collectors.toList()),
                createdAt,
                updatedAt
        );
    }

    @PrePersist
    void prePersist() {
        final var now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }

    public Set<TenantNetworkJpaEntity> getMemberships() { return memberships; }
}