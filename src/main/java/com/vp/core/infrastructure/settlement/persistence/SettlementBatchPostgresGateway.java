package com.vp.core.infrastructure.settlement.persistence;

import com.vp.core.domain.gateway.SettlementBatchGateway;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.settlement.SettlementBatch;
import com.vp.core.domain.settlement.SettlementBatchId;
import com.vp.core.domain.settlement.SettlementEntry;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.infrastructure.settlement.model.SettlementBatchJpaEntity;
import com.vp.core.infrastructure.settlement.model.SettlementEntryJpaEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class SettlementBatchPostgresGateway implements SettlementBatchGateway {

    private final SettlementBatchJpaRepository repository;

    public SettlementBatchPostgresGateway(final SettlementBatchJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public SettlementBatch create(final SettlementBatch batch) {
        final var entity = SettlementBatchJpaEntity.from(batch);
        final var saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SettlementBatch> findById(final SettlementBatchId id) {
        return repository.findByIdWithEntries(UUID.fromString(id.getValue()))
                .map(SettlementBatchJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SettlementBatch> findByPeriodKey(final String periodKey) {
        return repository.findByPeriodKey(periodKey)
                .map(SettlementBatchJpaEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SettlementBatch> findByTenantIdAndPeriodKey(final TenantId tenantId, final String periodKey) {
        return repository.findByTenantIdAndPeriodKey(
                        UUID.fromString(tenantId.getValue()),
                        periodKey
                )
                .map(SettlementBatchJpaEntity::toDomain);
    }

    @Override
    @Transactional
    public SettlementBatch update(final SettlementBatch batch) {
        final var existing = repository.findByIdWithEntries(UUID.fromString(batch.getId().getValue()))
                .orElseThrow(() -> new IllegalStateException("SettlementBatch not found: " + batch.getId().getValue()));

        existing.setStatus(batch.status().name());
        existing.setClosedAt(batch.closedAt());
        existing.setUpdatedAt(batch.getUpdatedAt());

        for (final SettlementEntry domainEntry : batch.entries()) {
            final var entryId = UUID.fromString(domainEntry.getId().getValue());
            existing.getEntries().stream()
                    .filter(e -> e.getId().equals(entryId))
                    .findFirst()
                    .ifPresent(entryEntity -> {
                        entryEntity.setStatus(domainEntry.status().name());
                        entryEntity.setPaidAt(domainEntry.paidAt());
                        entryEntity.setPaymentRef(domainEntry.paymentRef());
                        entryEntity.setUpdatedAt(domainEntry.getUpdatedAt());
                    });
        }

        final var saved = repository.save(existing);
        return saved.toDomain();
    }

    @Override
    @Transactional
    public void deleteById(final SettlementBatchId id) {
        repository.deleteById(UUID.fromString(id.getValue()));
    }

    @Override
    public Pagination<SettlementBatch> findAll(final SearchQuery query) {
        return Pagination.empty();
    }
}
