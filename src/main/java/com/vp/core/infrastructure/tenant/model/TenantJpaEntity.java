package com.vp.core.infrastructure.tenant.model;

import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.tenant.TenantStatus;
import com.vp.core.domain.valueObjects.*;

import com.vp.core.infrastructure.shared.BankAccountEmbeddable;
import com.vp.core.infrastructure.shared.LocationEmbeddable;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "tenants",
        indexes = {
                @Index(name = "idx_tenants_status", columnList = "status"),
                @Index(name = "idx_tenants_created_at", columnList = "created_at desc")
        }
)
public class TenantJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

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

    @Column(name = "logo_url")
    private String logoUrl;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected TenantJpaEntity() {
    }

    public static TenantJpaEntity from(final Tenant tenant) {
        final var e = new TenantJpaEntity();

        e.id = UUID.fromString(tenant.getId().getValue());

        e.name = tenant.getName();
        e.fantasyName = tenant.getFantasyName();
        e.documentValue = tenant.getDocument().getValue();

        final var loc = tenant.getLocation();
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

        final var bank = tenant.getBankAccount();
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

        e.status = tenant.getStatus().name();
        e.phone1 = tenant.getPhone1();
        e.phone2 = tenant.getPhone2();
        e.email = tenant.getEmail().getValue();
        e.url = tenant.getUrl().getValue();
        e.logoUrl = tenant.getLogoUrl();

        e.createdAt = tenant.getCreatedAt();
        e.updatedAt = tenant.getUpdatedAt();

        return e;
    }

    public Tenant toAggregate() {
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

        return Tenant.with(
                TenantId.from(String.valueOf(id)),
                name,
                fantasyName,
                Document.with(documentValue),
                loc,
                bank,
                TenantStatus.valueOf(status),
                phone1,
                phone2,
                Email.with(email),
                URL.with(url),
                logoUrl,
                createdAt,
                updatedAt
        );
    }

    public UUID getId() {
        return id;
    }
}