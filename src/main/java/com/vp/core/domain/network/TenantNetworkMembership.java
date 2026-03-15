package com.vp.core.domain.network;

import com.vp.core.domain.tenant.TenantId;

import java.time.Instant;

public class TenantNetworkMembership {

    private final TenantId tenantId;
    private MembershipRole role;
    private MembershipStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private TenantNetworkMembership(
            final TenantId tenantId,
            final MembershipRole role,
            final MembershipStatus status,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.tenantId = tenantId;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TenantNetworkMembership hostActive(final TenantId tenantId, final Instant now) {
        return new TenantNetworkMembership(tenantId, MembershipRole.HOST, MembershipStatus.ACTIVE, now, now);
    }

    public static TenantNetworkMembership memberPending(final TenantId tenantId, final Instant now) {
        return new TenantNetworkMembership(tenantId, MembershipRole.MEMBER, MembershipStatus.PENDING_INVITE, now, now);
    }

    public TenantId getTenantId() { return tenantId; }
    public MembershipRole getRole() { return role; }
    public MembershipStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setRole(final MembershipRole role, final Instant now) {
        this.role = role;
        this.updatedAt = now;
    }

    public void setStatus(final MembershipStatus status, final Instant now) {
        this.status = status;
        this.updatedAt = now;
    }

    public static TenantNetworkMembership with(
            final TenantId tenantId,
            final MembershipRole role,
            final MembershipStatus status,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new TenantNetworkMembership(tenantId, role, status, createdAt, updatedAt);
    }
}
