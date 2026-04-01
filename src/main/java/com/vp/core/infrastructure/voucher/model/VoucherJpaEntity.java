package com.vp.core.infrastructure.voucher.model;

import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.voucher.*;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "vouchers",
        indexes = {
                @Index(name = "idx_vouchers_campaign_id", columnList = "campaign_id"),
                @Index(name = "idx_vouchers_status", columnList = "status"),
                @Index(name = "idx_vouchers_expires_at", columnList = "expires_at"),
                @Index(name = "idx_vouchers_created_at", columnList = "created_at desc")
        }
)
public class VoucherJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "campaign_id", nullable = false, updatable = false)
    private UUID campaignId;

    @Column(name = "token_hash", nullable = false, updatable = false)
    private String tokenHash;

    @Column(name = "token_version", nullable = false, updatable = false)
    private int tokenVersion;

    @Column(name = "display_code", nullable = false, updatable = false)
    private String displayCode;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "buyer_phone")
    private String buyerPhone;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private Set<VoucherLedgerEntryJpaEntity> ledgerEntries = new HashSet<>();

    protected VoucherJpaEntity() {
    }

    public static VoucherJpaEntity from(final Voucher voucher) {
        final var e = new VoucherJpaEntity();

        e.id = UUID.fromString(voucher.getId().getValue());
        e.campaignId = UUID.fromString(voucher.campaignId().getValue());

        e.tokenHash = voucher.tokenHash();
        e.tokenVersion = voucher.tokenVersion();
        e.displayCode = voucher.displayCode();

        e.status = voucher.status().name();
        e.expiresAt = voucher.expiresAt();
        e.issuedAt = voucher.issuedAt();
        e.buyerName = voucher.buyerName();
        e.buyerPhone = voucher.buyerPhone();

        e.createdAt = voucher.getCreatedAt();
        e.updatedAt = voucher.getUpdatedAt();
        e.version  = voucher.getVersion();
        e.ledgerEntries = voucher.ledger().stream()
                .map(le -> VoucherLedgerEntryJpaEntity.from(e, le))
                .collect(Collectors.toSet());

        return e;
    }

    public Voucher toAggregate() {
        final var ledger = this.ledgerEntries.stream()
                .sorted(Comparator.comparing(VoucherLedgerEntryJpaEntity::getCreatedAt))
                .map(VoucherLedgerEntryJpaEntity::toDomain)
                .collect(Collectors.toList());

        return Voucher.with(
                VoucherId.from(String.valueOf(id)),
                CampaignId.from(String.valueOf(campaignId)),
                tokenHash,
                tokenVersion,
                displayCode,
                VoucherStatus.valueOf(status),
                expiresAt,
                ledger,
                createdAt,
                updatedAt,
                version,
                issuedAt,
                buyerName,
                buyerPhone
        );
    }

    public UUID getId() { return id; }

    public Set<VoucherLedgerEntryJpaEntity> getLedgerEntries() { return ledgerEntries; }
    public void setLedgerEntries(final Set<VoucherLedgerEntryJpaEntity> ledgerEntries) { this.ledgerEntries = ledgerEntries; }
}