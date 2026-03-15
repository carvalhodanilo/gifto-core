package com.vp.core.infrastructure.merchant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class MerchantNetworkId implements Serializable {

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "network_id", nullable = false)
    private UUID networkId;

    protected MerchantNetworkId() {
    }

    public MerchantNetworkId(final UUID merchantId, final UUID networkId) {
        this.merchantId = merchantId;
        this.networkId = networkId;
    }

    public UUID getMerchantId() {
        return merchantId;
    }

    public UUID getNetworkId() {
        return networkId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MerchantNetworkId that)) return false;
        return Objects.equals(merchantId, that.merchantId) && Objects.equals(networkId, that.networkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(merchantId, networkId);
    }
}