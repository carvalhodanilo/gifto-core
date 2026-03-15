package com.vp.core.domain.settlement;

import com.vp.core.domain.AggregateRoot;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.utils.InstantUtils;
import com.vp.core.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.*;

public class SettlementBatch extends AggregateRoot<SettlementBatchId> {

    private final TenantId tenantId;
    private final String periodKey;

    private SettlementBatchStatus status;
    private Instant closedAt;

    private final List<SettlementEntry> entries = new ArrayList<>();

    private SettlementBatch(
            final SettlementBatchId id,
            final TenantId tenantId,
            final String periodKey,
            final SettlementBatchStatus status,
            final Instant closedAt,
            final List<SettlementEntry> entries
    ) {
        super(id);
        this.tenantId = tenantId;
        this.periodKey = periodKey;
        this.status = status;
        this.closedAt = closedAt;

        if (entries != null) {
            this.entries.addAll(entries);
        }
    }

    public static SettlementBatch create(
            final TenantId tenantId,
            final String periodKey,
            final List<SettlementEntry> entries
    ) {
        return new SettlementBatch(
                SettlementBatchId.newId(),
                tenantId,
                periodKey,
                SettlementBatchStatus.OPEN,
                null,
                entries
        );
    }

    /** Reconstitution from persistence. */
    public static SettlementBatch with(
            final SettlementBatchId id,
            final TenantId tenantId,
            final String periodKey,
            final SettlementBatchStatus status,
            final Instant closedAt,
            final List<SettlementEntry> entries
    ) {
        return new SettlementBatch(id, tenantId, periodKey, status, closedAt, entries);
    }

    public void markEntryPaid(
            final SettlementEntryId entryId,
            final String paymentRef
    ) {
        if (this.status == SettlementBatchStatus.CLOSED) {
            throw new IllegalStateException("Batch is closed");
        }

        final var entry = findEntry(entryId)
                .orElseThrow(() -> new IllegalStateException("SettlementEntry not found"));

        entry.markPaid(paymentRef);

        if (allEntriesPaid()) {
            this.status = SettlementBatchStatus.CLOSED;
            this.closedAt = InstantUtils.now();
        }

        touch();
    }

    public void close() {
        if (entries.stream().anyMatch(e -> e.status() == SettlementEntryStatus.PENDING)) {
            throw new IllegalStateException("Cannot close batch with pending entries");
        }
        this.status = SettlementBatchStatus.CLOSED;
        this.closedAt = InstantUtils.now();
        touch();
    }

    private Optional<SettlementEntry> findEntry(final SettlementEntryId id) {
        return entries.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    private boolean allEntriesPaid() {
        return this.entries.stream()
                .allMatch(e -> e.status() == SettlementEntryStatus.PAID);
    }

    public TenantId tenantId() { return tenantId; }
    public String periodKey() { return periodKey; }
    public SettlementBatchStatus status() { return status; }
    public Instant closedAt() { return closedAt; }
    public List<SettlementEntry> entries() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public void validate(ValidationHandler handler) {
        // MVP: sem validações complexas
    }
}