package com.vp.core.domain.network;

import com.vp.core.domain.AggregateRoot;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.utils.InstantUtils;
import com.vp.core.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.*;

public class Network extends AggregateRoot<NetworkId> {

    private final String name;
    private final NetworkType type;       // PRIVATE|SHARED
    private final Map<TenantId, TenantNetworkMembership> memberships = new HashMap<>();

    private NetworkStatus status;   // ACTIVE|INACTIVE

    private Network(
            final NetworkId id,
            final String name,
            final NetworkType type,
            final NetworkStatus status,
            final Map<TenantId, TenantNetworkMembership> memberships
    ) {
        super(id);
        this.name = name;
        this.type = type;
        this.status = status;

        if (memberships != null) {
            this.memberships.putAll(memberships);
        }
    }

    private Network(
            final NetworkId id,
            final String name,
            final NetworkType type,
            final NetworkStatus status,
            final Map<TenantId, TenantNetworkMembership> memberships,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.type = type;
        this.status = status;

        if (memberships != null) {
            this.memberships.putAll(memberships);
        }
    }

    public static Network create(
            final NetworkId id,
            final String name,
            final NetworkType type,
            final TenantId hostTenantId
    ) {
        final Instant now = InstantUtils.now();

        final Map<TenantId, TenantNetworkMembership> memberships = new HashMap<>();
        memberships.put(
                hostTenantId,
                TenantNetworkMembership.hostActive(hostTenantId, now)
        );

        return new Network(
                id,
                name,
                type,
                NetworkStatus.ACTIVE,
                memberships
        );
    }

    public static Network createDefault(final TenantId hostTenantId) {
        final Instant now = InstantUtils.now();
        final NetworkId id = NetworkId.newId();

        final Map<TenantId, TenantNetworkMembership> memberships = new HashMap<>();
        memberships.put(
                hostTenantId,
                TenantNetworkMembership.hostActive(hostTenantId, now)
        );

        return new Network(
                id,
                "DEFAULT",
                NetworkType.PRIVATE,
                NetworkStatus.ACTIVE,
                memberships
        );
    }

    public static Network with(
            final NetworkId id,
            final String name,
            final NetworkType type,
            final NetworkStatus status,
            final Collection<TenantNetworkMembership> memberships,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        final Map<TenantId, TenantNetworkMembership> map = new HashMap<>();
        if (memberships != null) {
            for (TenantNetworkMembership m : memberships) {
                map.put(m.getTenantId(), m);
            }
        }

        return new Network(
                id,
                name,
                type,
                status,
                map,
                createdAt,
                updatedAt
        );
    }

    public void activate() {
        if (this.status == NetworkStatus.ACTIVE) return;
        this.status = NetworkStatus.ACTIVE;
        touch();
    }

    public void deactivate() {
        if (this.status == NetworkStatus.INACTIVE) return;
        this.status = NetworkStatus.INACTIVE;
        touch();
    }

    public void inviteTenant(final TenantId tenantId) {
        final Instant now = InstantUtils.now();
        final TenantNetworkMembership existing = memberships.get(tenantId);

        if (existing == null) {
            memberships.put(tenantId, TenantNetworkMembership.memberPending(tenantId, now));
        } else {
            existing.setStatus(MembershipStatus.PENDING_INVITE, now);
            existing.setRole(MembershipRole.MEMBER, now);
        }
        touch();
    }

    public void acceptInvite(final TenantId tenantId) {
        final Instant now = InstantUtils.now();
        final TenantNetworkMembership m = memberships.get(tenantId);
        if (m == null) return; // trocar por exception/validation
        m.setStatus(MembershipStatus.ACTIVE, now);
        touch();
    }

    public void suspendTenant(final TenantId tenantId) {
        final Instant now = InstantUtils.now();
        final TenantNetworkMembership m = memberships.get(tenantId);
        if (m == null) return;
        m.setStatus(MembershipStatus.SUSPENDED, now);
        touch();
    }

    public void removeTenant(final TenantId tenantId) {
        final Instant now = InstantUtils.now();
        final TenantNetworkMembership m = memberships.get(tenantId);
        if (m == null) return;
        m.setStatus(MembershipStatus.REMOVED, now);
        touch();
    }

    public void promoteToHost(final TenantId tenantId) {
        final Instant now = InstantUtils.now();
        final TenantNetworkMembership m = memberships.get(tenantId);
        if (m == null) return;
        m.setRole(MembershipRole.HOST, now);
        touch();
    }

    public String name() {
        return name;
    }

    public NetworkType type() {
        return type;
    }

    public NetworkStatus status() {
        return status;
    }

    public Collection<TenantNetworkMembership> memberships() {
        return Collections.unmodifiableCollection(memberships.values());
    }

    public Optional<TenantNetworkMembership> membershipOf(final TenantId tenantId) {
        return Optional.ofNullable(memberships.get(tenantId));
    }

    public boolean isTenantActiveMember(final TenantId tenantId) {
        final TenantNetworkMembership m = memberships.get(tenantId);
        return m != null && m.getStatus() == MembershipStatus.ACTIVE;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        // sem validações por enquanto
    }
}
