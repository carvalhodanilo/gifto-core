package com.vp.core.domain.merchant;

import com.vp.core.domain.network.NetworkId;
import com.vp.core.domain.utils.InstantUtils;

import java.time.Instant;
import java.util.Objects;

public final class MerchantNetworkLink {
    private final NetworkId networkId;
    private LinkStatus status;
    private final Instant joinedAt;
    private Instant updatedAt;

    private MerchantNetworkLink(final NetworkId networkId, final LinkStatus status, final Instant joinedAt, final Instant updatedAt) {
        this.networkId = Objects.requireNonNull(networkId, "networkId");
        this.status = Objects.requireNonNull(status, "status");
        this.joinedAt = Objects.requireNonNull(joinedAt, "joinedAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static MerchantNetworkLink active(final NetworkId networkId) {
        final Instant now = InstantUtils.now();
        return new MerchantNetworkLink(networkId, LinkStatus.ACTIVE, now, now);
    }

    public NetworkId networkId() {
        return networkId;
    }

    public LinkStatus status() {
        return status;
    }

    public Instant joinedAt() {
        return joinedAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return status == LinkStatus.ACTIVE;
    }

    public void activate() {
        if (status == LinkStatus.ACTIVE) return;
        status = LinkStatus.ACTIVE;
        updatedAt = InstantUtils.now();
    }

    public void deactivate() {
        if (status == LinkStatus.INACTIVE) return;
        status = LinkStatus.INACTIVE;
        updatedAt = InstantUtils.now();
    }

    public static MerchantNetworkLink with(
            final NetworkId networkId,
            final LinkStatus status,
            final Instant joinedAt,
            final Instant updatedAt
    ) {
        return new MerchantNetworkLink(
                networkId,
                status,
                joinedAt,
                updatedAt
        );
    }
}
