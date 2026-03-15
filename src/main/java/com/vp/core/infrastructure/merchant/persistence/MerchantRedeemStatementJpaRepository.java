package com.vp.core.infrastructure.merchant.persistence;

import com.vp.core.infrastructure.merchant.persistence.projection.MerchantRedeemProjection;
import com.vp.core.infrastructure.voucher.model.VoucherLedgerEntryJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface MerchantRedeemStatementJpaRepository extends Repository<VoucherLedgerEntryJpaEntity, UUID> {

    @Query(
            value = """
                SELECT
                  le.id                    AS ledgerEntryId,
                  le.voucher_id            AS voucherId,
                  v.display_code           AS displayCode,
                  le.amount_cents          AS amountCents,
                  le.created_at            AS createdAt,
                  se.id                    AS settlementEntryId,
                  se.settlement_batch_id   AS settlementBatchId,
                  se.status                AS settlementStatus,
                  se.paid_at               AS paidAt
                FROM voucher_ledger_entries le
                JOIN vouchers v
                  ON v.id = le.voucher_id
                LEFT JOIN settlement_entries se
                  ON se.id = le.settlement_entry_id
                WHERE (le.type = 'REDEEM' OR le.type = 'REVERSAL')
                  AND le.merchant_id = :merchantId
                  AND le.created_at >= :from
                  AND le.created_at <  :to
                  AND (
                    :statusFilter IS NULL
                    OR (:statusFilter = 'PAID' AND se.status = 'PAID')
                    OR (:statusFilter = 'PENDING' AND (se.id IS NULL OR se.status <> 'PAID'))
                  )
                ORDER BY le.created_at DESC
                """,
            countQuery = """
                SELECT COUNT(1)
                FROM voucher_ledger_entries le
                LEFT JOIN settlement_entries se
                  ON se.id = le.settlement_entry_id
                WHERE (le.type = 'REDEEM' OR le.type = 'REVERSAL')
                  AND le.merchant_id = :merchantId
                  AND le.created_at >= :from
                  AND le.created_at <  :to
                  AND (
                    :statusFilter IS NULL
                    OR (:statusFilter = 'PAID' AND se.status = 'PAID')
                    OR (:statusFilter = 'PENDING' AND (se.id IS NULL OR se.status <> 'PAID'))
                  )
                """,
            nativeQuery = true
    )
    Page<MerchantRedeemProjection> findRedeems(
            @Param("merchantId") UUID merchantId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("statusFilter") String statusFilter,
            Pageable pageable
    );

    interface TotalsProjection {
        long getGrossRedeemsCents();
        long getReversalsCents();
        long getNetSubtotalCents();
    }

    @Query(
            value = """
        SELECT
          COALESCE(SUM(CASE WHEN le.type = 'REDEEM' THEN le.amount_cents ELSE 0 END), 0) AS grossRedeemsCents,
          COALESCE(SUM(CASE WHEN le.type = 'REDEEM_REVERSAL' THEN le.amount_cents ELSE 0 END), 0) AS reversalsCents,
          COALESCE(
            SUM(CASE WHEN le.type = 'REDEEM' THEN le.amount_cents ELSE 0 END)
            -
            SUM(CASE WHEN le.type = 'REDEEM_REVERSAL' THEN le.amount_cents ELSE 0 END)
          , 0) AS netSubtotalCents
        FROM voucher_ledger_entries le
        LEFT JOIN settlement_entries se
          ON se.id = le.settlement_entry_id
        WHERE le.type IN ('REDEEM', 'REDEEM_REVERSAL')
          AND le.merchant_id = :merchantId
          AND le.created_at >= :from
          AND le.created_at <  :to
          AND (
            :statusFilter IS NULL
            OR (:statusFilter = 'PAID' AND se.status = 'PAID')
            OR (:statusFilter = 'PENDING' AND (se.id IS NULL OR se.status <> 'PAID'))
          )
        """,
            nativeQuery = true
    )
    TotalsProjection totals(
            @Param("merchantId") UUID merchantId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("statusFilter") String statusFilter
    );
}