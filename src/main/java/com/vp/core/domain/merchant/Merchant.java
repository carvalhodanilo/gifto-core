package com.vp.core.domain.merchant;

import com.vp.core.domain.AggregateRoot;
import com.vp.core.domain.network.NetworkId;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.utils.InstantUtils;
import com.vp.core.domain.validation.ValidationHandler;
import com.vp.core.domain.valueObjects.*;

import java.time.Instant;
import java.util.*;

/**
 * Merchant Aggregate Root
 *
 * Model:
 *  - TENANT owns MERCHANT
 *  - MERCHANT participates in many NETWORKs via MERCHANT_NETWORK (links)
 *
 * Invariants (inside aggregate):
 *  - merchant has an owner tenant
 *  - no duplicated network links (merchant_id, network_id)
 *  - cannot link/unlink when merchant is SUSPENDED (policy choice; you can relax if you want)
 *
 * Cross-aggregate rules (validated outside via Policy/DomainService):
 *  - tenant must have ACTIVE membership in the network to join it
 */
public class Merchant extends AggregateRoot<MerchantId> {

    private final TenantId tenantId;
    private String name;
    private String fantasyName;
    private Document document;
    private Location location;
    private BankAccount bankAccount;
    private MerchantStatus status;
    private String phone1;
    private String phone2;
    private Email email;
    private URL url;

    private final Map<NetworkId, MerchantNetworkLink> networkLinks = new HashMap<>();

    private Merchant(
            final MerchantId id,
            final TenantId tenantId,
            final String name,
            final String fantasyName,
            final Document document,
            final Location location,
            final BankAccount bankAccount,
            final MerchantStatus status,
            final String phone1,
            final String phone2,
            final Email email,
            final URL url,
            final Instant createdAt,
            final Instant updatedAt,
            final Map<NetworkId, MerchantNetworkLink> networkLinks
    ) {
        super(id, createdAt, updatedAt);

        this.tenantId = tenantId;
        this.name = name;
        this.fantasyName = fantasyName;
        this.document = document;
        this.location = location;
        this.bankAccount = bankAccount;
        this.status = status;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.email = email;
        this.url = url;
        this.networkLinks.putAll(networkLinks);
    }

    private Merchant(
            final MerchantId id,
            final TenantId tenantId,
            final String name,
            final String fantasyName,
            final Document document,
            final Location location,
            final BankAccount bankAccount,
            final MerchantStatus status,
            final String phone1,
            final String phone2,
            final Email email,
            final URL url
    ) {
        super(id);
        this.tenantId = tenantId;
        this.name = name;
        this.fantasyName = fantasyName;
        this.document = document;
        this.location = location;
        this.bankAccount = bankAccount;
        this.status = status;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.email = email;
        this.url = url;
    }

    public static Merchant create(
            final TenantId tenantId,
            final String name,
            final String fantasyName,
            final Document document,
            final Location location,
            final BankAccount bankAccount,
            final String phone1,
            final String phone2,
            final Email email,
            final URL url
    ) {
        final var id = MerchantId.newId();
        return new Merchant(
                id,
                tenantId,
                name,
                fantasyName,
                document,
                location,
                bankAccount,
                MerchantStatus.SUSPENDED,
                phone1,
                phone2,
                email,
                url
        );
    }

    public static Merchant with(
            final MerchantId id,
            final TenantId tenantId,
            final String name,
            final String fantasyName,
            final Document document,
            final Location location,
            final BankAccount bankAccount,
            final MerchantStatus status,
            final String phone1,
            final String phone2,
            final Email email,
            final URL url,
            final Instant createdAt,
            final Instant updatedAt,
            final Map<NetworkId, MerchantNetworkLink> networkLinks
    ) {
        return new Merchant(
                id,
                tenantId,
                name,
                fantasyName,
                document,
                location,
                bankAccount,
                status,
                phone1,
                phone2,
                email,
                url,
                createdAt,
                updatedAt,
                networkLinks
        );
    }

    public Merchant updateProfile(
            final String name,
            final String fantasyName,
            final String phone1,
            final String phone2,
            final Email email,
            final URL url
    ) {
        this.fantasyName = fantasyName;
        this.name = name;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.email = email;
        this.url = url;
        touch();
        return this;
    }

    public Merchant updateBankAccount(final BankAccount bankAccount) {
        this.bankAccount = bankAccount;
        touch();
        return this;
    }

    public Merchant updateLocation(final Location location) {
        this.location = location;
        touch();
        return this;
    }


    public void suspend() {
        if (this.status == MerchantStatus.SUSPENDED) return;
        this.status = MerchantStatus.SUSPENDED;
        touch();
    }

    public void activate() {
        if (this.status == MerchantStatus.ACTIVE) return;
        this.status = MerchantStatus.ACTIVE;
        touch();
    }

    public void joinNetwork(final NetworkId networkId) {
        ensureActive();
        Objects.requireNonNull(networkId, "networkId");

        final MerchantNetworkLink link = networkLinks.get(networkId);
        if (link == null) {
            networkLinks.put(networkId, MerchantNetworkLink.active(networkId));
            touch();
            return;
        }
        if (!link.isActive()) {
            link.activate();
            touch();
        }
    }

    public void leaveNetwork(final NetworkId networkId) {
        ensureActive();
        Objects.requireNonNull(networkId, "networkId");

        final MerchantNetworkLink link = networkLinks.get(networkId);
        if (link == null || !link.isActive()) return;

        link.deactivate();
        touch();
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String name() {
        return name;
    }

    public MerchantStatus status() {
        return status;
    }

    public boolean participatesIn(final NetworkId networkId) {
        final MerchantNetworkLink link = networkLinks.get(networkId);
        return link != null && link.isActive();
    }

    public Set<NetworkId> activeNetworks() {
        final Set<NetworkId> ids = new HashSet<>();
        for (MerchantNetworkLink link : networkLinks.values()) {
            if (link.isActive()) ids.add(link.networkId());
        }
        return Collections.unmodifiableSet(ids);
    }

    public Collection<MerchantNetworkLink> networkLinks() {
        return Collections.unmodifiableCollection(networkLinks.values());
    }

    private void ensureActive() {
        if (this.status != MerchantStatus.ACTIVE) {
            throw new IllegalStateException("Merchant is not ACTIVE");
        }
    }

    public boolean isActiveMemberOf(final NetworkId networkId) {
        final var link = this.networkLinks.get(networkId);
        return link != null && link.isActive();
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public String getFantasyName() {
        return fantasyName;
    }

    public Document getDocument() {
        return document;
    }

    public Location getLocation() {
        return location;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public MerchantStatus getStatus() {
        return status;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public Email getEmail() {
        return email;
    }

    public URL getUrl() {
        return url;
    }

    public Map<NetworkId, MerchantNetworkLink> getNetworkLinks() {
        return networkLinks;
    }

    @Override
    public void validate(ValidationHandler handler) {

    }
}
