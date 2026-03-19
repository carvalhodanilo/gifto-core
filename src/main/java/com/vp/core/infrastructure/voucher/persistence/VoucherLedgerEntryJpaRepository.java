package com.vp.core.infrastructure.voucher.persistence;

import com.vp.core.infrastructure.voucher.model.VoucherLedgerEntryJpaEntity;
import com.vp.core.infrastructure.voucher.persistence.projection.LedgerEntryListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoucherLedgerEntryJpaRepository extends JpaRepository<VoucherLedgerEntryJpaEntity, UUID> {

    Optional<VoucherLedgerEntryJpaEntity> findByVoucher_IdAndIdempotencyKey(final UUID voucherId, final String idempotencyKey);

    @Query("""
                select
                    le.id as ledgerEntryId,
                    le.type as type,
                    le.amountCents as amountCents,
                    le.createdAt as createdAt,
                    v.displayCode as displayCode
                from VoucherLedgerEntryJpaEntity le
                join le.voucher v
                where le.merchantId = :merchantId
                  and (
                        :search is null
                        or :search = ''
                        or cast(le.amountCents as string) = :search
                        or lower(v.displayCode) like lower(concat('%', :search, '%'))
                  )
            """)
    Page<LedgerEntryListProjection> findAllByMerchant(
            @Param("merchantId") UUID merchantId,
            @Param("search") String search,
            Pageable pageable
    );

    Page<VoucherLedgerEntryJpaEntity> findAll(Specification<VoucherLedgerEntryJpaEntity> whereClause, Pageable page);

    @Query(value = """
            SELECT le.id FROM voucher_ledger_entries le
            JOIN vouchers v ON v.id = le.voucher_id
            JOIN campaigns c ON c.id = v.campaign_id
            WHERE c.tenant_id = :tenantId
              AND le.type IN ('REDEEM', 'REVERSAL')
              AND le.settlement_entry_id IS NULL
              AND le.created_at >= :fromInclusive
              AND le.created_at < :toExclusive
            """, nativeQuery = true)
    List<UUID> findUnsettledRedeemAndReversalIds(
            @Param("tenantId") UUID tenantId,
            @Param("fromInclusive") Instant fromInclusive,
            @Param("toExclusive") Instant toExclusive
    );

    @Modifying
    @Query("UPDATE VoucherLedgerEntryJpaEntity le SET le.settlementEntryId = :settlementEntryId WHERE le.id IN :ledgerIds AND le.merchantId = :merchantId")
    int setSettlementEntryForMerchant(
            @Param("ledgerIds") List<UUID> ledgerIds,
            @Param("merchantId") UUID merchantId,
            @Param("settlementEntryId") UUID settlementEntryId
    );
}