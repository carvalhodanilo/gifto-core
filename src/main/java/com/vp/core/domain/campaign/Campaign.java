package com.vp.core.domain.campaign;

import com.vp.core.domain.AggregateRoot;
import com.vp.core.domain.network.NetworkId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.utils.InstantUtils;
import com.vp.core.domain.validation.ValidationHandler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Campaign Aggregate Root
 *
 * ER:
 *  - TENANT creates CAMPAIGN (campaign.tenantId)
 *  - CAMPAIGN targets NETWORK (campaign.networkId)
 */
public class Campaign extends AggregateRoot<CampaignId> {

    private final TenantId tenantId;
    private final NetworkId networkId;

    private String name;
    private int expirationDays;
    private Instant startsAt;
    private Instant endsAt;
    private CampaignStatus status;
    /** URL pública do banner na landing. */
    private String bannerUrl;
    /** URL da landing externa do parceiro (opcional); null = usar só a landing da plataforma. */
    private String externalLandingUrl;

    private long version;

    private Campaign(
            final CampaignId id,
            final TenantId tenantId,
            final NetworkId networkId,
            final String name,
            final int expirationDays,
            final Instant startsAt,
            final Instant endsAt,
            final CampaignStatus status,
            final String bannerUrl,
            final String externalLandingUrl
    ) {
        super(id);

        this.tenantId = tenantId;
        this.networkId = networkId;
        this.name = name;
        this.expirationDays = expirationDays;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = status;
        this.bannerUrl = bannerUrl;
        this.externalLandingUrl = externalLandingUrl;
    }

    private Campaign(
            final CampaignId id,
            final TenantId tenantId,
            final NetworkId networkId,
            final String name,
            final int expirationDays,
            final Instant startsAt,
            final Instant endsAt,
            final CampaignStatus status,
            final String bannerUrl,
            final String externalLandingUrl,
            final Instant createdAt,
            final Instant updatedAt,
            final long version
    ) {
        super(id, createdAt, updatedAt);

        this.tenantId = tenantId;
        this.networkId = networkId;
        this.name = name;
        this.expirationDays = expirationDays;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = status;
        this.bannerUrl = bannerUrl;
        this.externalLandingUrl = externalLandingUrl;
        this.version = version;
    }

    public static Campaign create(
            final TenantId tenantId,
            final NetworkId networkId,
            final String name,
            final int expirationDays,
            final Instant startsAt,
            final Instant endsAt,
            final String externalLandingUrl
    ) {
        final var id = CampaignId.newId();
        return new Campaign(
                id,
                tenantId,
                networkId,
                name,
                expirationDays,
                startsAt,
                endsAt,
                CampaignStatus.DRAFT,
                null,
                externalLandingUrl
        );
    }

    public static Campaign createDefault(
            final TenantId tenantId,
            final NetworkId networkId
    ) {
        final var id = CampaignId.newId();
        return new Campaign(
                id,
                tenantId,
                networkId,
                "DEFAULT_CAMPAIGN",
                60,
                InstantUtils.now(),
                InstantUtils.now().plus(10000, ChronoUnit.DAYS),
                CampaignStatus.DRAFT,
                null,
                null
        );
    }

    public Campaign updateBannerUrl(final String bannerUrl) {
        this.bannerUrl = bannerUrl;
        touch();
        return this;
    }

    public Campaign update(
            final String name,
            final int expirationDays,
            final Instant startsAt,
            final Instant endsAt,
            final String externalLandingUrl
    ) {
        this.name = name;
        this.expirationDays = expirationDays;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.externalLandingUrl = externalLandingUrl;
        touch();
        return this;
    }

    public void activate() {
        if (this.status == CampaignStatus.ACTIVE) return;
        this.status = CampaignStatus.ACTIVE;
        touch();
    }

    public void pause() {
        if (this.status == CampaignStatus.PAUSED) return;
        this.status = CampaignStatus.PAUSED;
        touch();
    }

    public void suspend() {
        if (this.status == CampaignStatus.ENDED) return;
        this.status = CampaignStatus.ENDED;
        touch();
    }

    public static Campaign with(
            final CampaignId id,
            final TenantId tenantId,
            final NetworkId networkId,
            final String name,
            final int expirationDays,
            final Instant startsAt,
            final Instant endsAt,
            final CampaignStatus status,
            final String bannerUrl,
            final String externalLandingUrl,
            final Instant createdAt,
            final Instant updatedAt,
            final long version
    ) {
        return new Campaign(
                id,
                tenantId,
                networkId,
                name,
                expirationDays,
                startsAt,
                endsAt,
                status,
                bannerUrl,
                externalLandingUrl,
                createdAt,
                updatedAt,
                version
        );
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public NetworkId networkId() {
        return networkId;
    }

    public String name() {
        return name;
    }

    public int expirationDays() {
        return expirationDays;
    }

    public Instant startsAt() {
        return startsAt;
    }

    public Instant endsAt() {
        return endsAt;
    }

    public CampaignStatus status() {
        return status;
    }

    public String bannerUrl() {
        return bannerUrl;
    }

    public String externalLandingUrl() {
        return externalLandingUrl;
    }

    public void ensureActive() {
        if (this.status != CampaignStatus.ACTIVE) {
            throw new IllegalStateException("Campaign is not ACTIVE");
        }
    }

    @Override
    public void validate(final ValidationHandler handler) {
        // sem validações por agora
    }

    public long getVersion() {
        return version;
    }
}
