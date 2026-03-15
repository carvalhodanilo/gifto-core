package com.vp.core.application.voucher.getLegderEntriesByMerchant.impl;

import com.vp.core.application.voucher.getLegderEntriesByMerchant.ListLedgerEntriesByMerchantCommand;
import com.vp.core.application.voucher.getLegderEntriesByMerchant.ListLedgerEntriesByMerchantOutput;
import com.vp.core.application.voucher.getLegderEntriesByMerchant.ListLedgerEntriesByMerchantUseCase;
import com.vp.core.domain.gateway.LedgerEntryGateway;
import com.vp.core.domain.pagination.Pagination;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ListLedgerEntriesByMerchantUseCaseImpl extends ListLedgerEntriesByMerchantUseCase {

    private final LedgerEntryGateway ledgerGateway;

    public ListLedgerEntriesByMerchantUseCaseImpl(
            final LedgerEntryGateway ledgerGateway
    ) {
        this.ledgerGateway = ledgerGateway;
    }

    @Override
    public Pagination<ListLedgerEntriesByMerchantOutput> execute(ListLedgerEntriesByMerchantCommand command) {
        final var merchantId = UUID.fromString(command.merchantId());
        return ledgerGateway.findAllByMerchant(merchantId, command.searchQuery())
                .map(ledgerEntry -> ListLedgerEntriesByMerchantOutput.of(
                        ledgerEntry.getLedgerEntryId().toString(),
                        ledgerEntry.getType(),
                        ledgerEntry.getAmountCents(),
                        String.valueOf(ledgerEntry.getCreatedAt()),
                        ledgerEntry.getDisplayCode()
                ));
    }

}