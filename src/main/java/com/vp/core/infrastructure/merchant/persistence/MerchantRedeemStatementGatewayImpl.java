package com.vp.core.infrastructure.merchant.persistence;

import com.vp.core.domain.gateway.MerchantRedeemStatementGateway;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.infrastructure.merchant.persistence.projection.MerchantRedeemProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class MerchantRedeemStatementGatewayImpl implements MerchantRedeemStatementGateway {

    private final MerchantRedeemStatementJpaRepository repo;

    public MerchantRedeemStatementGatewayImpl(final MerchantRedeemStatementJpaRepository repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    @Override
    public Pagination<RedeemRow> findRedeems(
            final MerchantId merchantId,
            final Instant from,
            final Instant to,
            final SettlementFilter filter,
            final SearchQuery query
    ) {
        Objects.requireNonNull(merchantId, "merchantId must not be null");
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");
        Objects.requireNonNull(query, "query must not be null");

        final int page = Math.max(query.page(), 0);
        final int perPage = clampPerPage(query.perPage());
        final String statusFilter = (filter == null ? null : filter.name());

        final var pageable = PageRequest.of(page, perPage);
        final var result = repo.findRedeems(
                UUID.fromString(merchantId.getValue()),
                from,
                to,
                statusFilter,
                pageable
        );

        final var items = result.getContent().stream()
                .map(this::toRow)
                .toList();

        return new Pagination<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                items
        );
    }

    @Override
    public Totals totals(
            final MerchantId merchantId,
            final Instant from,
            final Instant to,
            final SettlementFilter filter
    ) {
        Objects.requireNonNull(merchantId, "merchantId must not be null");
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");

        final String statusFilter = (filter == null ? null : filter.name());
        final var t = repo.totals(UUID.fromString(merchantId.getValue()), from, to, statusFilter);

        return new Totals(
                t.getGrossRedeemsCents(),
                t.getReversalsCents(),
                t.getNetSubtotalCents()
        );
    }

    private RedeemRow toRow(final MerchantRedeemProjection p) {
        return new RedeemRow(
                p.getLedgerEntryId().toString(),
                p.getVoucherId().toString(),
                p.getDisplayCode(),
                p.getAmountCents(),
                p.getCreatedAt(),
                p.getSettlementEntryId() == null ? null : p.getSettlementEntryId().toString(),
                p.getSettlementBatchId() == null ? null : p.getSettlementBatchId().toString(),
                p.getSettlementStatus(),
                p.getPaidAt()
        );
    }

    private int clampPerPage(final int perPage) {
        if (perPage <= 0) return 20;
        return Math.min(perPage, 100);
    }
}