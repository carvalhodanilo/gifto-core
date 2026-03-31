package com.vp.core.infrastructure.tenant;

import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.pagination.SearchTenantQuery;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.infrastructure.tenant.model.TenantJpaEntity;
import com.vp.core.infrastructure.tenant.persistence.TenantJpaRepository;
import com.vp.core.infrastructure.tenant.persistence.projection.TenantListProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenantPostgresGateway implements TenantGateway {

    private final TenantJpaRepository repository;

    public TenantPostgresGateway(final TenantJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tenant create(final Tenant tenant) {
        final var saved = repository.save(TenantJpaEntity.from(tenant));
        return saved.toAggregate();
    }

    @Override
    public Tenant update(final Tenant tenant) {
        final var saved = repository.save(TenantJpaEntity.from(tenant));
        return saved.toAggregate();
    }

    @Override
    public void deleteById(final TenantId anId) {
        repository.deleteById(UUID.fromString(anId.getValue()));
    }

    @Override
    public Optional<Tenant> findById(final TenantId anId) {
        return repository.findById(UUID.fromString(anId.getValue()))
                .map(TenantJpaEntity::toAggregate);
    }

    @Override
    public Pagination<Tenant> findAll(final SearchQuery aQuery) {
        throw new UnsupportedOperationException("Pagination not implemented yet");
    }

    @Override
    public Pagination<TenantListProjection> findAllPaged(final SearchTenantQuery query) {
        final var pageable = PageRequest.of(query.page(), query.perPage());
        final var result = repository.findAllPaged(
                query.name(),
                query.document(),
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
    public boolean existsByDocumentValue(final String documentValue) {
        return repository.existsByDocumentValue(documentValue);
    }

    @Override
    public boolean existsByEmail(final String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public List<Tenant> findAllActive() {
        return repository.findAllByStatus("ACTIVE").stream()
                .map(TenantJpaEntity::toAggregate)
                .toList();
    }
}