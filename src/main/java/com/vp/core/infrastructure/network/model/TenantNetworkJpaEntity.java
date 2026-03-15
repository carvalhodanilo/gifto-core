package com.vp.core.infrastructure.network.model;

import com.vp.core.domain.network.MembershipRole;
import com.vp.core.domain.network.MembershipStatus;
import com.vp.core.domain.network.TenantNetworkMembership;
import com.vp.core.domain.tenant.TenantId;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "tenant_networks",
        indexes = {
                @Index(name = "idx_tenant_networks_network_id", columnList = "network_id"),
                @Index(name = "idx_tenant_networks_status", columnList = "status")
        }
)
public class TenantNetworkJpaEntity {

    @EmbeddedId
    private TenantNetworkId id;

    @MapsId("networkId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "network_id", nullable = false)
    private NetworkJpaEntity network;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TenantNetworkJpaEntity() {
    }

    public static TenantNetworkJpaEntity from(final NetworkJpaEntity network, final TenantNetworkMembership membership) {
        final var e = new TenantNetworkJpaEntity();
        e.network = network;

        final UUID tenantId = UUID.fromString(membership.getTenantId().getValue());
        e.id = new TenantNetworkId(tenantId, network.getId());

        e.role = membership.getRole().name();
        e.status = membership.getStatus().name();
        e.createdAt = membership.getCreatedAt();
        e.updatedAt = membership.getUpdatedAt();

        return e;
    }

    public TenantNetworkMembership toDomain() {
        // precisa do método "with" no domínio (vou te passar abaixo)
        return TenantNetworkMembership.with(
                TenantId.from(String.valueOf(id.getTenantId())),
                MembershipRole.valueOf(role),
                MembershipStatus.valueOf(status),
                createdAt,
                updatedAt
        );
    }
}