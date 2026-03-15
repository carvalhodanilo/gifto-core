package com.vp.core.infrastructure.campaign.persistence;

import com.vp.core.infrastructure.campaign.model.CampaignJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignJpaRepository extends JpaRepository<CampaignJpaEntity, UUID> {

    Optional<CampaignJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);
    List<CampaignJpaEntity> findAllByTenantIdAndStatus(UUID tenantId, String status);
    List<CampaignJpaEntity> findAllByTenantIdAndStatusNot(UUID tenantId, String status);
}