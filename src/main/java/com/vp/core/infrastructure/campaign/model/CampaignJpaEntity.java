package com.vp.core.infrastructure.campaign.model;

import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.campaign.CampaignStatus;
import com.vp.core.domain.network.NetworkId;
import com.vp.core.domain.tenant.TenantId;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "campaigns",
        indexes = {
                @Index(name = "idx_campaigns_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_campaigns_network_id", columnList = "network_id"),
                @Index(name = "idx_campaigns_status", columnList = "status"),
                @Index(name = "idx_campaigns_created_at", columnList = "created_at desc"),
                @Index(name = "idx_campaigns_starts_at", columnList = "starts_at"),
                @Index(name = "idx_campaigns_ends_at", columnList = "ends_at")
        }
)
public class CampaignJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "network_id", nullable = false, updatable = false)
    private UUID networkId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "expiration_days", nullable = false)
    private int expirationDays;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    @Column(name = "status", nullable = false)
    private String status;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CampaignJpaEntity() {}

    public static CampaignJpaEntity from(final Campaign campaign) {
        final var e = new CampaignJpaEntity();

        e.id = UUID.fromString(campaign.getId().getValue());
        e.tenantId = UUID.fromString(campaign.tenantId().getValue());
        e.networkId = UUID.fromString(campaign.networkId().getValue());

        e.name = campaign.name();
        e.expirationDays = campaign.expirationDays();
        e.startsAt = campaign.startsAt();
        e.endsAt = campaign.endsAt();
        e.status = campaign.status().name();

        e.createdAt = campaign.getCreatedAt();
        e.updatedAt = campaign.getUpdatedAt();

        e.version = campaign.getVersion();

        return e;
    }

    public Campaign toAggregate() {
        return Campaign.with(
                CampaignId.from(String.valueOf(id)),
                TenantId.from(String.valueOf(tenantId)),
                NetworkId.from(String.valueOf(networkId)),
                name,
                expirationDays,
                startsAt,
                endsAt,
                CampaignStatus.valueOf(status),
                createdAt,
                updatedAt,
                version
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
}