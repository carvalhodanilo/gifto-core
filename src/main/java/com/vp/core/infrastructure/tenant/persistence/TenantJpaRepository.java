package com.vp.core.infrastructure.tenant.persistence;

import com.vp.core.infrastructure.tenant.model.TenantJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TenantJpaRepository extends JpaRepository<TenantJpaEntity, UUID> {

    boolean existsByDocumentValue(String documentValue);

    boolean existsByEmail(String email);

    List<TenantJpaEntity> findAllByStatus(String status);
}