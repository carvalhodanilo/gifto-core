package com.vp.core.infrastructure.settlement.model;

import com.vp.core.domain.settlement.SettlementBatch;
import com.vp.core.domain.settlement.SettlementBatchId;
import com.vp.core.domain.settlement.SettlementBatchStatus;
import com.vp.core.domain.settlement.SettlementEntry;
import com.vp.core.domain.tenant.TenantId;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "settlement_batches",
        indexes = {
                @Index(name = "idx_settlement_batches_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_settlement_batches_period_key", columnList = "period_key"),
                @Index(name = "idx_settlement_batches_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_settlement_batches_tenant_period", columnNames = {"tenant_id", "period_key"})
        }
)
public class SettlementBatchJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Column(name = "period_key", nullable = false, updatable = false)
    private String periodKey;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "settlementBatch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt")
    private List<SettlementEntryJpaEntity> entries = new ArrayList<>();

    protected SettlementBatchJpaEntity() {
    }

    public static SettlementBatchJpaEntity from(final SettlementBatch batch) {
        final var e = new SettlementBatchJpaEntity();
        e.id = UUID.fromString(batch.getId().getValue());
        e.tenantId = UUID.fromString(batch.tenantId().getValue());
        e.periodKey = batch.periodKey();
        e.status = batch.status().name();
        e.closedAt = batch.closedAt();
        e.createdAt = batch.getCreatedAt();
        e.updatedAt = batch.getUpdatedAt();
        e.version = 0;
        for (final var entry : batch.entries()) {
            e.entries.add(SettlementEntryJpaEntity.from(e, entry));
        }
        return e;
    }

    public SettlementBatch toDomain() {
        final var domainEntries = entries.stream()
                .map(SettlementEntryJpaEntity::toDomain)
                .collect(Collectors.toList());

        return SettlementBatch.with(
                SettlementBatchId.from(id.toString()),
                TenantId.from(tenantId.toString()),
                periodKey,
                SettlementBatchStatus.valueOf(status),
                closedAt,
                domainEntries
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getPeriodKey() {
        return periodKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(final Instant closedAt) {
        this.closedAt = closedAt;
    }

    public long getVersion() {
        return version;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<SettlementEntryJpaEntity> getEntries() {
        return entries;
    }
}
