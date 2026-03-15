package com.vp.core.application.merchant.statement.impl;

import com.vp.core.application.merchant.statement.ListMerchantRedeemsByPeriodCommand;
import com.vp.core.application.merchant.statement.ListMerchantRedeemsByPeriodOutput;
import com.vp.core.application.merchant.statement.ListMerchantRedeemsByPeriodUseCase;
import com.vp.core.domain.gateway.MerchantRedeemStatementGateway;
import com.vp.core.domain.merchant.MerchantId;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ListMerchantRedeemsByPeriodUseCaseImpl extends ListMerchantRedeemsByPeriodUseCase {

    private final MerchantRedeemStatementGateway gateway;

    public ListMerchantRedeemsByPeriodUseCaseImpl(final MerchantRedeemStatementGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Override
    public ListMerchantRedeemsByPeriodOutput execute(final ListMerchantRedeemsByPeriodCommand cmd) {
        if (cmd.from() == null || cmd.to() == null || !cmd.from().isBefore(cmd.to())) {
            throw new IllegalArgumentException("from must be before to");
        }

        final var merchantId = MerchantId.from(cmd.merchantId());
        final MerchantRedeemStatementGateway.SettlementFilter filter =
                cmd.settlementStatus() == null
                        ? null
                        : MerchantRedeemStatementGateway.SettlementFilter.valueOf(cmd.settlementStatus().name());

        final var pagination = gateway.findRedeems(
                merchantId,
                cmd.from(),
                cmd.to(),
                filter,
                cmd.query()
        );

        final var totals = gateway.totals(
                merchantId,
                cmd.from(),
                cmd.to(),
                filter
        );

        final var gross = totals.getGrossRedeemsCents();
        final var reversals = totals.getReversalsCents();
        final var netSubtotal = totals.getNetSubtotalCents();

        final var mapped = pagination.map(r -> new ListMerchantRedeemsByPeriodOutput.RedeemItem(
                r.ledgerEntryId(),
                r.voucherId(),
                r.displayCode(),
                r.amountCents(),
                r.createdAt(),
                new ListMerchantRedeemsByPeriodOutput.Settlement(
                        isPaid(r.settlementStatus())
                                ? ListMerchantRedeemsByPeriodOutput.Settlement.SettlementStatus.PAID
                                : ListMerchantRedeemsByPeriodOutput.Settlement.SettlementStatus.PENDING,
                        r.settlementEntryId(),
                        r.settlementBatchId(),
                        r.paidAt()
                )
        ));

        return new ListMerchantRedeemsByPeriodOutput(
                new ListMerchantRedeemsByPeriodOutput.Period(cmd.from(), cmd.to()),
                new ListMerchantRedeemsByPeriodOutput.Summary(gross, reversals, netSubtotal),
                mapped
        );
    }

    private boolean isPaid(final String settlementStatus) {
        return settlementStatus != null && settlementStatus.equalsIgnoreCase("PAID");
    }
}