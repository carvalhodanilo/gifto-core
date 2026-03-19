package com.vp.core.application.settlement.runBatch.impl;

import com.vp.core.application.settlement.runBatch.RunSettlementBatchCommand;
import com.vp.core.application.settlement.runBatch.RunSettlementBatchOutput;
import com.vp.core.application.settlement.runBatch.RunSettlementBatchUseCase;
import com.vp.core.domain.gateway.LedgerEntryGateway;
import com.vp.core.domain.gateway.SettlementBatchGateway;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.settlement.PeriodKey;
import com.vp.core.domain.settlement.SettlementBatch;
import com.vp.core.domain.settlement.SettlementEntry;
import com.vp.core.domain.voucher.LedgerEntry;
import com.vp.core.domain.voucher.LedgerEntryType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RunSettlementBatchUseCaseImpl extends RunSettlementBatchUseCase {

    private final SettlementBatchGateway settlementGateway;
    private final LedgerEntryGateway ledgerGateway;

    public RunSettlementBatchUseCaseImpl(
            final SettlementBatchGateway settlementGateway,
            final LedgerEntryGateway ledgerGateway
    ) {
        this.settlementGateway = settlementGateway;
        this.ledgerGateway = ledgerGateway;
    }

    @Override
    public RunSettlementBatchOutput execute(RunSettlementBatchCommand command) {
        final var tenantId = command.tenantId();
        final var periodKey = PeriodKey.previous();
        final var periodKeyValue = periodKey.getValue();

        settlementGateway.findByTenantIdAndPeriodKey(tenantId, periodKeyValue)
                .ifPresent(b -> {
                    throw new IllegalStateException("Settlement batch already exists for period " + periodKeyValue + ".");
                });

        // 2️⃣ Buscar ledger elegível do período anterior
        final var ledgerEntries = ledgerGateway
                .findUnsettledRedeemAndReversal(tenantId, periodKey);

        if (ledgerEntries.isEmpty()) {
            throw new IllegalStateException("No ledger entries to settle");
        }

        // 3️⃣ Agrupar por merchant
        final Map<MerchantId, List<LedgerEntry>> grouped =
                ledgerEntries.stream()
                        .collect(Collectors.groupingBy(LedgerEntry::merchantId));

        final List<SettlementEntry> entries = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            final var merchantId = entry.getKey();
            final var merchantLedgers = entry.getValue();

            long gross = merchantLedgers.stream()
                    .filter(l -> l.type() == LedgerEntryType.REDEEM)
                    .mapToLong(LedgerEntry::amountCents)
                    .sum();

            long reversals = merchantLedgers.stream()
                    .filter(l -> l.type() == LedgerEntryType.REVERSAL)
                    .mapToLong(LedgerEntry::amountCents)
                    .sum();

            long fees = calculateFee(gross - reversals);

            long net = gross - reversals - fees;

            if (net < 0) {
                net = 0; // MVP simplificado
            }

            entries.add(
                    SettlementEntry.create(
                            merchantId,
                            gross,
                            reversals,
                            fees
                    )
            );
        }

        final var batch = SettlementBatch.create(
                tenantId,
                periodKeyValue,
                entries
        );
        final var createdBatch = settlementGateway.create(batch);

        ledgerGateway.markAsSettled(
                ledgerEntries.stream().map(LedgerEntry::id).toList(),
                createdBatch.getId()
        );

        return RunSettlementBatchOutput.of(createdBatch.getId().getValue());
    }

    private long calculateFee(long gross) {
        final double percent = 0.05; // 5%
        return Math.round(gross * percent);
    }
}
