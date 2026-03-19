package com.vp.core.infrastructure.voucher;

import com.vp.core.domain.gateway.LedgerEntryGateway;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.settlement.PeriodKey;
import com.vp.core.domain.settlement.SettlementBatchId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.voucher.LedgerEntry;
import com.vp.core.domain.voucher.LedgerEntryId;
import com.vp.core.infrastructure.settlement.persistence.SettlementBatchJpaRepository;
import com.vp.core.infrastructure.voucher.model.VoucherLedgerEntryJpaEntity;
import com.vp.core.infrastructure.voucher.persistence.VoucherLedgerEntryJpaRepository;
import com.vp.core.infrastructure.voucher.persistence.VoucherJpaRepository;
import com.vp.core.infrastructure.voucher.persistence.projection.LedgerEntryListProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LedgerEntryPostgresGateway implements LedgerEntryGateway {

    private final VoucherLedgerEntryJpaRepository repository;
    private final VoucherJpaRepository voucherRepository;
    private final SettlementBatchJpaRepository settlementBatchRepository;

    public LedgerEntryPostgresGateway(
            final VoucherLedgerEntryJpaRepository repository,
            final VoucherJpaRepository voucherRepository,
            final SettlementBatchJpaRepository settlementBatchRepository
    ) {
        this.repository = repository;
        this.voucherRepository = voucherRepository;
        this.settlementBatchRepository = settlementBatchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntry> findUnsettledRedeemAndReversal(final TenantId tenantId, final PeriodKey periodKey) {
        final var fromInclusive = periodKey.getStartDateInclusive().atStartOfDay(ZoneOffset.UTC).toInstant();
        final var toExclusive = periodKey.getEndDateInclusive().plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        final var ids = repository.findUnsettledRedeemAndReversalIds(
                UUID.fromString(tenantId.getValue()),
                fromInclusive,
                toExclusive
        );
        if (ids.isEmpty()) {
            return List.of();
        }
        return repository.findAllById(ids).stream()
                .map(VoucherLedgerEntryJpaEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void markAsSettled(final List<LedgerEntryId> ids, final SettlementBatchId batchId) {
        if (ids.isEmpty()) {
            return;
        }
        final var batch = settlementBatchRepository.findByIdWithEntries(UUID.fromString(batchId.getValue()))
                .orElseThrow(() -> new IllegalStateException("SettlementBatch not found: " + batchId.getValue()));

        final var ledgerIds = ids.stream()
                .map(id -> UUID.fromString(id.getValue()))
                .toList();

        for (final var entry : batch.getEntries()) {
            repository.setSettlementEntryForMerchant(
                    ledgerIds,
                    entry.getMerchantId(),
                    entry.getId()
            );
        }
    }

    @Override
    public Pagination<LedgerEntryListProjection> findAllByMerchant(UUID merchantId, SearchQuery searchQuery) {
        final var page = PageRequest.of(
                searchQuery.page(),
                searchQuery.perPage(),
                Sort.by(Sort.Direction.fromString(searchQuery.direction()), searchQuery.sort())
        );

        final var pageResult =
                this.repository.findAllByMerchant(merchantId, searchQuery.terms(), page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getContent()
        );
    }

    @Override
    @Transactional
    public LedgerEntry create(final LedgerEntry entry) {
        final var voucher = voucherRepository.findById(UUID.fromString(entry.voucherId().getValue()))
                .orElseThrow(() -> new IllegalStateException("Voucher not found: " + entry.voucherId().getValue()));
        final var entity = VoucherLedgerEntryJpaEntity.from(voucher, entry);
        final var saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    @Transactional
    public void deleteById(final LedgerEntryId anId) {
        repository.deleteById(UUID.fromString(anId.getValue()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LedgerEntry> findById(final LedgerEntryId anId) {
        return repository.findById(UUID.fromString(anId.getValue()))
                .map(VoucherLedgerEntryJpaEntity::toDomain);
    }

    @Override
    @Transactional
    public LedgerEntry update(final LedgerEntry entry) {
        final var existing = repository.findById(UUID.fromString(entry.id().getValue()))
                .orElseThrow(() -> new IllegalStateException("LedgerEntry not found: " + entry.id().getValue()));
        final var voucher = existing.getVoucher();
        final var entity = VoucherLedgerEntryJpaEntity.from(voucher, entry);
        entity.setSettlementEntryId(existing.getSettlementEntryId());
        final var saved = repository.save(entity);
        return saved.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public Pagination<LedgerEntry> findAll(final SearchQuery searchQuery) {
        return Pagination.empty();
    }
}
