package com.vp.core.infrastructure.voucher;

import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.pagination.SearchVoucherQuery;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.voucher.Voucher;
import com.vp.core.domain.gateway.VoucherGateway;
import com.vp.core.domain.voucher.VoucherId;
import com.vp.core.infrastructure.voucher.model.VoucherJpaEntity;
import com.vp.core.infrastructure.voucher.persistence.VoucherJpaRepository;

import com.vp.core.infrastructure.voucher.persistence.projection.VoucherIssuedProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class VoucherPostgresGateway implements VoucherGateway {

    private final VoucherJpaRepository voucherRepository;

    public VoucherPostgresGateway(final VoucherJpaRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    @Transactional
    public Voucher create(final Voucher voucher) {
        final var entity = VoucherJpaEntity.from(voucher);
        final var saved = voucherRepository.save(entity);
        return saved.toAggregate();
    }

    @Override
    public void deleteById(VoucherId anId) {

    }

    @Override
    @Transactional
    public Voucher update(final Voucher voucher) {
        final var entity = VoucherJpaEntity.from(voucher);
        final var saved = voucherRepository.save(entity);
        return saved.toAggregate();
    }

    @Override
    public Pagination<Voucher> findAll(SearchQuery aQuery) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Voucher> findById(final VoucherId id) {
        return voucherRepository.findById(UUID.fromString(id.getValue()))
                .map(VoucherJpaEntity::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Voucher> findByTokenHash(final String tokenHash) {
        return voucherRepository.findByTokenHash(tokenHash)
                .map(VoucherJpaEntity::toAggregate);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Voucher> findByTokenHashAndDisplayCode(final String tokenHash, final String displayCode) {
        return voucherRepository.findByTokenHashAndDisplayCode(tokenHash, displayCode)
                .map(VoucherJpaEntity::toAggregate);
    }

    @Override
    public Optional<Voucher> findByDisplayCode(String displayCode) {
        return voucherRepository.findByDisplayCode(displayCode)
                .map(VoucherJpaEntity::toAggregate);
    }

    @Override
    public Pagination<VoucherIssuedProjection> findAllByTenant(String tenantId, SearchVoucherQuery searchQuery) {
        var result = voucherRepository.findIssuedVouchersByTenant(
                UUID.fromString(tenantId),
                searchQuery.campaignName(),
                searchQuery.active() ? "ACTIVE" : null,
                searchQuery.displayCode(),
                searchQuery.buyerName(),
                searchQuery.buyerPhone(),
                PageRequest.of(searchQuery.page() , searchQuery.perPage())
        );

        return new Pagination<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getContent()
        );
    }
}