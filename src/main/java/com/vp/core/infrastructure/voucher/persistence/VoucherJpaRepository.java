package com.vp.core.infrastructure.voucher.persistence;

import com.vp.core.infrastructure.voucher.model.VoucherJpaEntity;
import com.vp.core.infrastructure.voucher.persistence.projection.VoucherIssuedProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VoucherJpaRepository extends JpaRepository<VoucherJpaEntity, UUID> {

    Optional<VoucherJpaEntity> findByTokenHash(final String tokenHash);

    Optional<VoucherJpaEntity> findByTokenHashAndDisplayCode(final String tokenHash, final String displayCode);

    @EntityGraph(attributePaths = "ledgerEntries")
    Optional<VoucherJpaEntity> findByDisplayCode(final String displayCode);

    @Query(
            value = """
            select
                v.id as id,
                v.campaign_id as campaignId,
                c.name as campaignName,
                v.display_code as displayCode,
                v.status as status,
                v.issued_at as issuedAt,
                v.expires_at as expiresAt,
                (select coalesce(sum(le.amount_cents), 0)
                 from voucher_ledger_entries le where le.voucher_id = v.id and le.type = 'ISSUE') as amountCents
            from vouchers v
            join campaigns c on c.id = v.campaign_id
            where c.tenant_id = :tenantId
              and (:campaignName is null or lower(c.name) like lower(concat('%', :campaignName, '%')))
              and (:status is null or v.status = :status)
              and (:displayCode is null or lower(v.display_code) like lower(concat('%', :displayCode, '%')))
            order by v.issued_at desc
        """,
            countQuery = """
            select count(*)
            from vouchers v
            join campaigns c on c.id = v.campaign_id
            where c.tenant_id = :tenantId
              and (:campaignName is null or lower(c.name) like lower(concat('%', :campaignName, '%')))
              and (:status is null or v.status = :status)
              and (:displayCode is null or lower(v.display_code) like lower(concat('%', :displayCode, '%')))
        """,
            nativeQuery = true
    )
    Page<VoucherIssuedProjection> findIssuedVouchersByTenant(
            @Param("tenantId") UUID tenantId,
            @Param("campaignName") String campaignName,
            @Param("status") String status,
            @Param("displayCode") String displayCode,
            Pageable pageable
    );
}