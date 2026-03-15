package com.vp.core.application.settlement.markAsPaid.impl;

import com.vp.core.application.settlement.markAsPaid.*;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.SettlementBatchGateway;
import com.vp.core.domain.validation.DomainError;
import org.springframework.stereotype.Service;

@Service
public class MarkSettlementEntryAsPaidUseCaseImpl extends MarkSettlementEntryAsPaidUseCase {

    private final SettlementBatchGateway settlementGateway;

    public MarkSettlementEntryAsPaidUseCaseImpl(
            final SettlementBatchGateway settlementGateway
    ) {
        this.settlementGateway = settlementGateway;
    }

    @Override
    public MarkSettlementEntryAsPaidOutput execute(final MarkSettlementEntryAsPaidCommand command) {
        final var batch = settlementGateway
                .findById(command.batchId())
                .orElseThrow(() -> NotFoundException.with(
                        new DomainError("Settlement batch not found: " + command.batchId().getValue())
                ));

        batch.markEntryPaid(
                command.entryId(),
                command.paymentRef()
        );

        settlementGateway.update(batch);

        return MarkSettlementEntryAsPaidOutput.from(
                batch.getId(),
                command.entryId()
        );
    }
}
