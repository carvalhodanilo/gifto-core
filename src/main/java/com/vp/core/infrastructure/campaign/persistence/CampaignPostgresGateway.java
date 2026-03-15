package com.vp.core.infrastructure.campaign.persistence;

import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.infrastructure.campaign.model.CampaignJpaEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CampaignPostgresGateway implements CampaignGateway {

    private final CampaignJpaRepository repository;

    public CampaignPostgresGateway(final CampaignJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Campaign> findByTenantIdAndId(final TenantId tenantId, final CampaignId campaignId) {
        return repository.findByTenantIdAndId(
                        UUID.fromString(tenantId.getValue()),
                        UUID.fromString(campaignId.getValue())
                )
                .map(CampaignJpaEntity::toAggregate);
    }

    @Override
    public List<Campaign> findAllActiveByTenantId(TenantId tenantId) {
        return repository.findAllByTenantIdAndStatus(UUID.fromString(tenantId.getValue()), "ACTIVE")
                .stream()
                .map(CampaignJpaEntity::toAggregate)
                .toList();
    }

    @Override
    public List<Campaign> findAllByTenantId(TenantId tenantId) {
        return repository.findAllByTenantIdAndStatusNot(UUID.fromString(tenantId.getValue()), "ENDED")
                .stream()
                .map(CampaignJpaEntity::toAggregate)
                .toList();
    }

    @Override
    public Campaign create(final Campaign campaign) {
        final var saved = repository.save(CampaignJpaEntity.from(campaign));
        return saved.toAggregate();
    }

    @Override
    public void deleteById(final CampaignId anId) {
        repository.deleteById(UUID.fromString(anId.getValue()));
    }

    @Override
    public Optional<Campaign> findById(final CampaignId anId) {
        return repository.findById(UUID.fromString(anId.getValue()))
                .map(CampaignJpaEntity::toAggregate);
    }

    @Override
    public Campaign update(final Campaign campaign) {
        final var saved = repository.save(CampaignJpaEntity.from(campaign));
        return saved.toAggregate();
    }

    @Override
    public Pagination<Campaign> findAll(final SearchQuery aQuery) {
        throw new UnsupportedOperationException("Pagination not implemented yet");
    }
}