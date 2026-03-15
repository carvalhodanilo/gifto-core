package com.vp.core.infrastructure.network.persistence;

import com.vp.core.domain.gateway.NetworkGateway;
import com.vp.core.domain.network.*;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.infrastructure.network.model.NetworkJpaEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class NetworkPostgresGateway implements NetworkGateway {

    private final NetworkJpaRepository repository;

    public NetworkPostgresGateway(final NetworkJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Network> findDefaultByTenantId(final TenantId tenantId) {
        return repository.findDefaultForTenant(
                        UUID.fromString(tenantId.getValue()),
                        "DEFAULT",
                        NetworkType.PRIVATE.name(),
                        NetworkStatus.ACTIVE.name(),
                        MembershipRole.HOST.name(),
                        MembershipStatus.ACTIVE.name()
                )
                .map(NetworkJpaEntity::toAggregate);
    }

    @Override
    public Network create(final Network network) {
        final var saved = repository.save(NetworkJpaEntity.from(network));
        return saved.toAggregate();
    }

    @Override
    public void deleteById(final NetworkId anId) {
        repository.deleteById(UUID.fromString(anId.getValue()));
    }

    @Override
    public Optional<Network> findById(final NetworkId anId) {
        return repository.findById(UUID.fromString(anId.getValue()))
                .map(NetworkJpaEntity::toAggregate);
    }

    @Override
    public Network update(final Network network) {
        final var saved = repository.save(NetworkJpaEntity.from(network));
        return saved.toAggregate();
    }

    @Override
    public Pagination<Network> findAll(final SearchQuery aQuery) {
        throw new UnsupportedOperationException("Pagination not implemented yet");
    }
}