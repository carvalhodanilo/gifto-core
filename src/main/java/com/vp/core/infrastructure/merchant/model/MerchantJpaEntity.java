package com.vp.core.infrastructure.merchant.model;

import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.merchant.MerchantNetworkLink;
import com.vp.core.domain.merchant.MerchantStatus;
import com.vp.core.domain.network.NetworkId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.*;

import com.vp.core.infrastructure.shared.BankAccountEmbeddable;
import com.vp.core.infrastructure.shared.LocationEmbeddable;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "merchants",
        indexes = {
                @Index(name = "idx_merchants_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_merchants_status", columnList = "status"),
                @Index(name = "idx_merchants_created_at", columnList = "created_at desc")
        }
)
public class MerchantJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "fantasy_name")
    private String fantasyName;

    @Column(name = "document_value", nullable = false)
    private String documentValue;

    @Embedded
    private LocationEmbeddable location;

    @Embedded
    private BankAccountEmbeddable bankAccount;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "phone1")
    private String phone1;

    @Column(name = "phone2")
    private String phone2;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "landing_logo_url")
    private String landingLogoUrl;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private Set<MerchantNetworkJpaEntity> networkLinks = new HashSet<>();

    protected MerchantJpaEntity() {
    }

    public static MerchantJpaEntity from(final Merchant merchant) {
        final var e = new MerchantJpaEntity();

        e.id = UUID.fromString(merchant.getId().getValue());
        e.tenantId = UUID.fromString(merchant.tenantId().getValue());

        e.name = merchant.name();
        e.fantasyName = merchant.getFantasyName();
        e.documentValue = merchant.getDocument().getValue();

        final var loc = merchant.getLocation();
        e.location = (loc == null) ? null : LocationEmbeddable.of(
                loc.getStreet(),
                loc.getNumber(),
                loc.getNeighborhood(),
                loc.getComplement(),
                loc.getCity(),
                loc.getState(),
                loc.getCountry(),
                loc.getPostalCode()
        );

        final var bank = merchant.getBankAccount();
        if (bank == null) {
            e.bankAccount = null;
        } else {
            e.bankAccount = BankAccountEmbeddable.of(
                    bank.getBankCode(),
                    bank.getBankName(),
                    bank.getBranch(),
                    bank.getAccountNumber(),
                    bank.getAccountDigit(),
                    bank.getAccountType() != null ? bank.getAccountType().name() : null,
                    bank.getHolderName(),
                    bank.getHolderDocument() != null ? bank.getHolderDocument().getValue() : null,
                    bank.getPixKey() != null ? bank.getPixKey().getType().name() : null,
                    bank.getPixKey() != null ? bank.getPixKey().getValue() : null
            );
        }

        e.status = merchant.status().name();
        e.phone1 = merchant.getPhone1();
        e.phone2 = merchant.getPhone2();
        e.email = merchant.getEmail().getValue();
        e.url = merchant.getUrl().getValue();
        e.landingLogoUrl = merchant.getLandingLogoUrl();

        e.createdAt = merchant.getCreatedAt();
        e.updatedAt = merchant.getUpdatedAt();

        e.networkLinks = merchant.networkLinks().stream()
                .map(link -> MerchantNetworkJpaEntity.from(e, link))
                .collect(Collectors.toSet());

        return e;
    }

    /**
     * Atualiza os campos mutáveis a partir do aggregate, preservando id, tenantId, version, createdAt e networkLinks.
     * Deve ser usado em update para não perder o @Version e causar erro de optimistic lock.
     */
    public void applyFrom(final Merchant merchant) {
        this.name = merchant.name();
        this.fantasyName = merchant.getFantasyName();
        this.documentValue = merchant.getDocument().getValue();

        final var loc = merchant.getLocation();
        this.location = (loc == null) ? null : LocationEmbeddable.of(
                loc.getStreet(),
                loc.getNumber(),
                loc.getNeighborhood(),
                loc.getComplement(),
                loc.getCity(),
                loc.getState(),
                loc.getCountry(),
                loc.getPostalCode()
        );

        final var bank = merchant.getBankAccount();
        if (bank == null) {
            this.bankAccount = null;
        } else {
            this.bankAccount = BankAccountEmbeddable.of(
                    bank.getBankCode(),
                    bank.getBankName(),
                    bank.getBranch(),
                    bank.getAccountNumber(),
                    bank.getAccountDigit(),
                    bank.getAccountType() != null ? bank.getAccountType().name() : null,
                    bank.getHolderName(),
                    bank.getHolderDocument() != null ? bank.getHolderDocument().getValue() : null,
                    bank.getPixKey() != null ? bank.getPixKey().getType().name() : null,
                    bank.getPixKey() != null ? bank.getPixKey().getValue() : null
            );
        }

        this.status = merchant.status().name();
        this.phone1 = merchant.getPhone1();
        this.phone2 = merchant.getPhone2();
        this.email = merchant.getEmail().getValue();
        this.url = merchant.getUrl().getValue();
        this.landingLogoUrl = merchant.getLandingLogoUrl();
        this.updatedAt = merchant.getUpdatedAt();
    }

    public Merchant toAggregate() {
        final Location loc = (location == null)
                ? Location.empty()
                : Location.with(
                location.getStreet(),
                location.getNumber(),
                location.getNeighborhood(),
                location.getComplement(),
                location.getCity(),
                location.getState(),
                location.getCountry(),
                location.getPostalCode()
        );

        final BankAccount bank;
        if (bankAccount == null) {
            bank = BankAccount.empty();
        } else {
            final PixKey pixKey =
                    (bankAccount.getPixKeyType() != null && bankAccount.getPixKeyValue() != null)
                            ? PixKey.of(PixKey.PixKeyType.valueOf(bankAccount.getPixKeyType()), bankAccount.getPixKeyValue())
                            : null;

            bank = BankAccount.of(
                    bankAccount.getBankCode(),
                    bankAccount.getBankName(),
                    bankAccount.getBranch(),
                    bankAccount.getAccountNumber(),
                    bankAccount.getAccountDigit(),
                    bankAccount.getAccountType() != null ? AccountType.valueOf(bankAccount.getAccountType()) : null,
                    bankAccount.getHolderName(),
                    bankAccount.getHolderDocumentValue() != null ? Document.with(bankAccount.getHolderDocumentValue()) : null,
                    pixKey
            );
        }

        return Merchant.with(
                MerchantId.from(String.valueOf(id)),
                TenantId.from(String.valueOf(tenantId)),
                name,
                fantasyName,
                Document.with(documentValue),
                loc,
                bank,
                MerchantStatus.valueOf(status),
                phone1,
                phone2,
                Email.with(email),
                URL.with(url),
                landingLogoUrl,
                createdAt,
                updatedAt,
                toDomainNetworkLinks(networkLinks)
        );
    }

    private static Map<NetworkId, MerchantNetworkLink> toDomainNetworkLinks(final Set<MerchantNetworkJpaEntity> entities) {
        return entities.stream()
                .map(MerchantNetworkJpaEntity::toDomain)
                .collect(Collectors.toMap(MerchantNetworkLink::networkId, link -> link));
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

    public UUID getId() { return id; }
    public Set<MerchantNetworkJpaEntity> getNetworkLinks() { return networkLinks; }
}