package com.vp.core.infrastructure.merchant.persistence;

import com.vp.core.infrastructure.merchant.model.MerchantJpaEntity;
import com.vp.core.infrastructure.merchant.persistence.projection.MerchantListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            select
                m.id as id,
                m.fantasyName as fantasyName,
                m.status as status
            from MerchantJpaEntity m
            where m.tenantId = :tenantId
              and (:terms is null or :terms = '' or lower(m.name) like lower(concat('%', :terms, '%'))
                   or lower(m.fantasyName) like lower(concat('%', :terms, '%')))
              and (:status is null or :status = '' or m.status = :status)
            order by m.fantasyName asc nulls last, m.name asc
            """)
    Page<MerchantListProjection> findAllByTenantId(
            @Param("tenantId") UUID tenantId,
            @Param("terms") String terms,
            @Param("status") String status,
            Pageable pageable
    );
}