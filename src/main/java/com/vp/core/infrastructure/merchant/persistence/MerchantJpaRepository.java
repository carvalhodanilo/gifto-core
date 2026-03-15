package com.vp.core.infrastructure.merchant.persistence;

import com.vp.core.infrastructure.merchant.model.MerchantJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantJpaRepository
        extends JpaRepository<MerchantJpaEntity, UUID> {

    @EntityGraph(attributePaths = { "networkLinks" })
    Optional<MerchantJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByTenantIdAndDocumentValue(UUID tenantId, String documentValue);

    boolean existsByTenantIdAndEmail(UUID tenantId, String email);

    List<MerchantJpaEntity> findAllByTenantIdAndStatus(UUID tenantId, String status);
}