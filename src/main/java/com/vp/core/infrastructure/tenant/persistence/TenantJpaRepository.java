package com.vp.core.infrastructure.tenant.persistence;

import com.vp.core.infrastructure.tenant.model.TenantJpaEntity;
import com.vp.core.infrastructure.tenant.persistence.projection.TenantListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TenantJpaRepository extends JpaRepository<TenantJpaEntity, UUID> {

    boolean existsByDocumentValue(String documentValue);

    boolean existsByEmail(String email);

    List<TenantJpaEntity> findAllByStatus(String status);

    @Query("""
            select
                t.id as id,
                t.name as name,
                t.fantasyName as fantasyName,
                t.documentValue as documentValue,
                t.logoUrl as logoUrl,
                t.status as status
            from TenantJpaEntity t
            where (:name is null or :name = '' or lower(t.name) like lower(concat('%', :name, '%'))
                   or lower(t.fantasyName) like lower(concat('%', :name, '%')))
              and (:document is null or :document = '' or t.documentValue like concat('%', :document, '%'))
            order by t.createdAt desc
            """)
    Page<TenantListProjection> findAllPaged(
            @Param("name") String name,
            @Param("document") String document,
            Pageable pageable
    );
}