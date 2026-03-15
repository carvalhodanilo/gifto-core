package com.vp.core.infrastructure.merchant.model;

import com.vp.core.domain.merchant.LinkStatus;
import com.vp.core.domain.merchant.MerchantNetworkLink;
import com.vp.core.domain.network.NetworkId;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "merchant_networks",
        indexes = {
                @Index(name = "idx_merchant_networks_network_id", columnList = "network_id"),
                @Index(name = "idx_merchant_networks_status", columnList = "status"),
                @Index(name = "idx_merchant_networks_updated_at", columnList = "updated_at desc")
        }
)
public class MerchantNetworkJpaEntity {

    @EmbeddedId
    private MerchantNetworkId id;

    @MapsId("merchantId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private MerchantJpaEntity merchant;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected MerchantNetworkJpaEntity() {
    }

    public static MerchantNetworkJpaEntity from(final MerchantJpaEntity merchant, final MerchantNetworkLink link) {
        final var merchantEntity = new MerchantNetworkJpaEntity();
        merchantEntity.merchant = merchant;

        final UUID networkId = UUID.fromString(link.networkId().getValue());
        merchantEntity.id = new MerchantNetworkId(merchant.getId(), networkId);

        merchantEntity.status = link.status().name();
        merchantEntity.joinedAt = link.joinedAt();
        merchantEntity.updatedAt = link.updatedAt();
        return merchantEntity;
    }

    public MerchantNetworkLink toDomain() {
        return MerchantNetworkLink.with(
                NetworkId.from(String.valueOf(getNetworkId())),
                LinkStatus.valueOf(status),
                joinedAt,
                updatedAt
        );
    }

    public UUID getNetworkId() {
        return id.getNetworkId();
    }

    public String getStatus() {
        return status;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}