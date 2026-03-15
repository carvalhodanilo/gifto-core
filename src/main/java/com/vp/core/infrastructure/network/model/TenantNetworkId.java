package com.vp.core.infrastructure.network.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TenantNetworkId implements Serializable {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "network_id", nullable = false)
    private UUID networkId;

    protected TenantNetworkId() {}

    public TenantNetworkId(final UUID tenantId, final UUID networkId) {
        this.tenantId = tenantId;
        this.networkId = networkId;
    }

    public UUID getTenantId() { return tenantId; }

    public UUID getNetworkId() { return networkId; }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TenantNetworkId that)) return false;
        return Objects.equals(tenantId, that.tenantId) && Objects.equals(networkId, that.networkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, networkId);
    }
}