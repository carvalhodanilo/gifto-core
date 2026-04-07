package com.vp.core.domain.tenant;

import com.vp.core.domain.AggregateRoot;
import com.vp.core.domain.validation.ValidationHandler;
import com.vp.core.domain.valueObjects.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Tenant extends AggregateRoot<TenantId> {

    private String name;
    private String fantasyName;
    private Document document;
    private Location location;
    private BankAccount bankAccount;
    private TenantStatus status;
    private String phone1;
    private String phone2;
    private Email email;
    private URL url;
    /** URL pública (S3) do logo para header / landing. */
    private String logoUrl;
    /** Cor principal (#RGB / #RRGGBB). Null = cliente usa default da app. */
    private String primaryBrandColor;
    /** Cor secundária. Null = default da app. */
    private String secondaryBrandColor;

    private Tenant(
            final TenantId id,
            final String name,
            final String fantasyName,
            final Document document,
            final Location location,
            final BankAccount bankAccount,
            final TenantStatus status,
            final String phone1,
            final String phone2,
            final Email email,
            final URL url,
            final String logoUrl,
            final String primaryBrandColor,
            final String secondaryBrandColor,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        super(id, createdAt, updatedAt);

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
        this.logoUrl = logoUrl;
        this.primaryBrandColor = primaryBrandColor;
        this.secondaryBrandColor = secondaryBrandColor;
    }

    private Tenant(
            final TenantId id,
            final String name,
            final String fantasyName,
            final Document document,
            final Location location,
            final BankAccount bankAccount,
            final TenantStatus status,
            final String phone1,
            final String phone2,
            final Email email,
            final URL url,
            final String logoUrl,
            final String primaryBrandColor,
            final String secondaryBrandColor
    ) {
        super(id);

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
        this.logoUrl = logoUrl;
        this.primaryBrandColor = primaryBrandColor;
        this.secondaryBrandColor = secondaryBrandColor;
    }

    public static Tenant create(
            final String name,
            final String fantasyName,
            final Document document,
            final String phone1,
            final Email email,
            final URL url
    ) {
        final var id = TenantId.newId();
        return new Tenant(
                id,
                name,
                fantasyName,
                document,
                Location.empty(),
                BankAccount.empty(),
                TenantStatus.ACTIVE,
                phone1,
                null,
                email,
                url,
                null,
                null,
                null
        );
    }

    public Tenant updateLogoUrl(final String logoUrl) {
        this.logoUrl = logoUrl;
        touch();
        return this;
    }

    public Tenant updateBrandColors(final String primaryBrandColor, final String secondaryBrandColor) {
        this.primaryBrandColor = normalizeHexColor(primaryBrandColor);
        this.secondaryBrandColor = normalizeHexColor(secondaryBrandColor);
        touch();
        return this;
    }

    private static String normalizeHexColor(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        final var s = value.trim();
        if (!s.startsWith("#") || (s.length() != 4 && s.length() != 7)) {
            throw new IllegalArgumentException("Cor inválida: use #RGB ou #RRGGBB.");
        }
        for (int i = 1; i < s.length(); i++) {
            final char c = s.charAt(i);
            final boolean hex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
            if (!hex) {
                throw new IllegalArgumentException("Cor inválida: use apenas dígitos hexadecimais.");
            }
        }
        return s;
    }

    public Tenant updateProfile(
            final String name,
            final String fantasyName,
            final String phone1,
            final String phone2,
            final Email email,
            final URL url
    ) {
        this.name = name;
        this.fantasyName = fantasyName;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.email = email;
        this.url = url;
        touch();
        return this;
    }

    public Tenant updateBankAccount(final BankAccount bankAccount) {
        this.bankAccount = bankAccount;
        touch();
        return this;
    }

    public Tenant updateLocation(final Location location) {
        this.location = location;
        touch();
        return this;
    }

    public static Tenant with(
            final TenantId id,
            final String name,
            final String fantasyName,
            final Document document,
            final Location location,
            final BankAccount bankAccount,
            final TenantStatus status,
            final String phone1,
            final String phone2,
            final Email email,
            final URL url,
            final String logoUrl,
            final String primaryBrandColor,
            final String secondaryBrandColor,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new Tenant(
                id,
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
                logoUrl,
                primaryBrandColor,
                secondaryBrandColor,
                createdAt,
                updatedAt
        );
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

    public TenantStatus getStatus() {
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

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getPrimaryBrandColor() {
        return primaryBrandColor;
    }

    public String getSecondaryBrandColor() {
        return secondaryBrandColor;
    }

    public void suspend() {
        if (this.status == TenantStatus.SUSPENDED) return;
        this.status = TenantStatus.SUSPENDED;
        touch();
    }

    public void activate() {
        if (this.status == TenantStatus.ACTIVE) return;
        this.status = TenantStatus.ACTIVE;
        touch();
    }

    @Override
    public void validate(final ValidationHandler handler) {
    }
}
