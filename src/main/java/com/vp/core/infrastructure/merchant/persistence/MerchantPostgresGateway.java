package com.vp.core.infrastructure.merchant.persistence;

import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchMerchantQuery;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.infrastructure.merchant.model.MerchantJpaEntity;
import com.vp.core.infrastructure.merchant.persistence.projection.MerchantListProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MerchantPostgresGateway implements MerchantGateway {

    private final MerchantJpaRepository repository;

    public MerchantPostgresGateway(final MerchantJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Merchant create(final Merchant merchant) {
        final var entity = MerchantJpaEntity.from(merchant);
        final var saved = repository.save(entity);
        return saved.toAggregate();
    }

    @Override
    public Merchant update(final Merchant merchant) {
        final var entity = repository.findByIdAndTenantId(
                        UUID.fromString(merchant.getId().getValue()),
                        UUID.fromString(merchant.tenantId().getValue())
                )
                .orElseThrow(() -> NotFoundException.with(Merchant.class, merchant.getId()));
        entity.applyFrom(merchant);
        final var saved = repository.save(entity);
        return saved.toAggregate();
    }

    @Override
    public void deleteById(final MerchantId anId) {
        repository.deleteById(UUID.fromString(anId.getValue()));
    }

    @Override
    public Optional<Merchant> findById(final MerchantId anId) {
        return repository.findById(UUID.fromString(anId.getValue()))
                .map(MerchantJpaEntity::toAggregate);
    }

    @Override
    public Optional<Merchant> findByIdAndTenantId(
            final MerchantId anId,
            final TenantId tenantId
    ) {
        return repository.findByIdAndTenantId(
                        UUID.fromString(anId.getValue()),
                        UUID.fromString(tenantId.getValue())
                )
                .map(MerchantJpaEntity::toAggregate);
    }

    @Override
    public boolean existsByTenantIdAndDocumentValue(
            final TenantId tenantId,
            final String documentValue
    ) {
        return repository.existsByTenantIdAndDocumentValue(
                UUID.fromString(tenantId.getValue()),
                documentValue
        );
    }

    @Override
    public boolean existsByTenantIdAndEmailValue(
            final TenantId tenantId,
            final String email
    ) {
        return repository.existsByTenantIdAndEmail(
                UUID.fromString(tenantId.getValue()),
                email
        );
    }

    @Override
    public List<Merchant> findAllActiveByTenantId(TenantId tenantId) {
        return repository.findAllByTenantIdAndStatus(UUID.fromString(tenantId.getValue()), "ACTIVE")
                .stream()
                .map(MerchantJpaEntity::toAggregate)
                .toList();
    }

    @Override
    public Pagination<MerchantListProjection> findAllByTenantId(TenantId tenantId, SearchMerchantQuery query) {
        var pageable = PageRequest.of(query.page(), query.perPage());
        var result = repository.findAllByTenantId(
                UUID.fromString(tenantId.getValue()),
                query.terms(),
                query.status(),
                pageable
        );
        return new Pagination<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getContent()
        );
    }

    @Override
    public Pagination<Merchant> findAll(final SearchQuery aQuery) {
        throw new UnsupportedOperationException("Pagination not implemented yet");
    }
}