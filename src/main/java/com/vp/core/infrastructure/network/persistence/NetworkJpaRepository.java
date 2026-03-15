package com.vp.core.infrastructure.network.persistence;

import com.vp.core.infrastructure.network.model.NetworkJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface NetworkJpaRepository extends JpaRepository<NetworkJpaEntity, UUID> {

    @EntityGraph(attributePaths = { "memberships" })
    Optional<NetworkJpaEntity> findById(UUID id);

    @EntityGraph(attributePaths = { "memberships" })
    @Query("""
        select n
        from NetworkJpaEntity n
        join n.memberships m
        where n.name = :name
          and n.type = :type
          and n.status = :status
          and m.id.tenantId = :tenantId
          and m.role = :role
          and m.status = :membershipStatus
    """)
    Optional<NetworkJpaEntity> findDefaultForTenant(
            @Param("tenantId") UUID tenantId,
            @Param("name") String name,
            @Param("type") String type,
            @Param("status") String status,
            @Param("role") String role,
            @Param("membershipStatus") String membershipStatus
    );
}