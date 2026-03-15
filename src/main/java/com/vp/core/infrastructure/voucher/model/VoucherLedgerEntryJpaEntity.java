package com.vp.core.infrastructure.voucher.model;

import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.voucher.*;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "voucher_ledger_entries",
        indexes = {
                @Index(name = "idx_ledger_voucher_id", columnList = "voucher_id"),
                @Index(name = "idx_ledger_voucher_created_at", columnList = "voucher_id, created_at desc"),
                @Index(name = "idx_ledger_merchant_id", columnList = "merchant_id"),
                @Index(name = "idx_ledger_ref_entry", columnList = "ref_ledger_entry_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_ledger_voucher_idempotency", columnNames = {"voucher_id", "idempotency_key"})
        }
)
public class VoucherLedgerEntryJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "voucher_id", nullable = false, updatable = false)
    private VoucherJpaEntity voucher;

    @Column(name = "type", nullable = false, updatable = false)
    private String type;

    @Column(name = "amount_cents", nullable = false, updatable = false)
    private long amountCents;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "ref_ledger_entry_id")
    private UUID refLedgerEntryId;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "settlement_entry_id")
    private UUID settlementEntryId;

    protected VoucherLedgerEntryJpaEntity() {
    }

    public static VoucherLedgerEntryJpaEntity from(final VoucherJpaEntity voucher, final LedgerEntry entry) {
        final var e = new VoucherLedgerEntryJpaEntity();

        e.id = UUID.fromString(entry.id().getValue());
        e.voucher = voucher;

        e.type = entry.type().name();
        e.amountCents = entry.amountCents();

        e.merchantId = entry.merchantId() == null ? null : UUID.fromString(entry.merchantId().getValue());
        e.refLedgerEntryId = entry.refLedgerEntryId() == null ? null : UUID.fromString(entry.refLedgerEntryId().getValue());

        e.idempotencyKey = entry.idempotencyKey();
        e.createdAt = entry.createdAt();

        return e;
    }

    public LedgerEntry toDomain() {
        return LedgerEntry.with(
                LedgerEntryId.from(String.valueOf(id)),
                VoucherId.from(String.valueOf(voucher.getId())),
                LedgerEntryType.valueOf(type),
                amountCents,
                merchantId == null ? null : MerchantId.from(String.valueOf(merchantId)),
                refLedgerEntryId == null ? null : LedgerEntryId.from(String.valueOf(refLedgerEntryId)),
                idempotencyKey,
                createdAt
        );
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public Instant getCreatedAt() { return createdAt; }

    public VoucherJpaEntity getVoucher() { return voucher; }

    public UUID getSettlementEntryId() { return settlementEntryId; }

    public void setSettlementEntryId(final UUID settlementEntryId) { this.settlementEntryId = settlementEntryId; }
}