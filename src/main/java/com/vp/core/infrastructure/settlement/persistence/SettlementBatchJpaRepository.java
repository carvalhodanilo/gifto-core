package com.vp.core.infrastructure.settlement.persistence;

import com.vp.core.infrastructure.settlement.model.SettlementBatchJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SettlementBatchJpaRepository extends JpaRepository<SettlementBatchJpaEntity, UUID> {

    @Query("select b from SettlementBatchJpaEntity b left join fetch b.entries where b.id = :id")
    Optional<SettlementBatchJpaEntity> findByIdWithEntries(@Param("id") UUID id);

    Optional<SettlementBatchJpaEntity> findByPeriodKey(String periodKey);

    Optional<SettlementBatchJpaEntity> findByTenantIdAndPeriodKey(UUID tenantId, String periodKey);
}
