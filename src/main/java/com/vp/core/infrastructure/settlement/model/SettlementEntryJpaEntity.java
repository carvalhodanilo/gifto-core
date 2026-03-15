package com.vp.core.infrastructure.settlement.model;

import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.settlement.SettlementEntry;
import com.vp.core.domain.settlement.SettlementEntryId;
import com.vp.core.domain.settlement.SettlementEntryStatus;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "settlement_entries",
        indexes = {
                @Index(name = "idx_settlement_entries_batch_id", columnList = "settlement_batch_id"),
                @Index(name = "idx_settlement_entries_merchant_id", columnList = "merchant_id"),
                @Index(name = "idx_settlement_entries_status", columnList = "status")
        }
)
public class SettlementEntryJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_batch_id", nullable = false, updatable = false)
    private SettlementBatchJpaEntity settlementBatch;

    @Column(name = "merchant_id", nullable = false, updatable = false)
    private UUID merchantId;

    @Column(name = "gross_cents", nullable = false, updatable = false)
    private long grossCents;

    @Column(name = "reversals_cents", nullable = false, updatable = false)
    private long reversalsCents;

    @Column(name = "fees_cents", nullable = false, updatable = false)
    private long feesCents;

    @Column(name = "net_cents", nullable = false, updatable = false)
    private long netCents;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "payment_ref")
    private String paymentRef;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SettlementEntryJpaEntity() {
    }

    public static SettlementEntryJpaEntity from(final SettlementBatchJpaEntity batchEntity, final SettlementEntry entry) {
        final var e = new SettlementEntryJpaEntity();
        e.id = UUID.fromString(entry.getId().getValue());
        e.settlementBatch = batchEntity;
        e.merchantId = UUID.fromString(entry.merchantId().getValue());
        e.grossCents = entry.grossCents();
        e.reversalsCents = entry.reversalsCents();
        e.feesCents = entry.feesCents();
        e.netCents = entry.netCents();
        e.status = entry.status().name();
        e.paidAt = entry.paidAt();
        e.paymentRef = entry.paymentRef();
        e.createdAt = entry.getCreatedAt() != null ? entry.getCreatedAt() : Instant.now();
        e.updatedAt = entry.getUpdatedAt() != null ? entry.getUpdatedAt() : Instant.now();
        return e;
    }

    public SettlementEntry toDomain() {
        return SettlementEntry.with(
                SettlementEntryId.from(id.toString()),
                MerchantId.from(merchantId.toString()),
                grossCents,
                reversalsCents,
                feesCents,
                netCents,
                SettlementEntryStatus.valueOf(status),
                paidAt,
                paymentRef
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getMerchantId() {
        return merchantId;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public void setPaidAt(final Instant paidAt) {
        this.paidAt = paidAt;
    }

    public void setPaymentRef(final String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
