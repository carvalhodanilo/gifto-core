package com.vp.core.application.voucher.reversal.impl;

import com.vp.core.application.VoucherTokenService;
import com.vp.core.application.voucher.reversal.ReverseRedeemCommand;
import com.vp.core.application.voucher.reversal.ReverseRedeemOutput;
import com.vp.core.application.voucher.reversal.ReverseRedeemUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.VoucherGateway;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.validation.DomainError;
import com.vp.core.domain.voucher.LedgerEntryId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReverseRedeemUseCaseImpl extends ReverseRedeemUseCase {

    private final VoucherGateway voucherGateway;
    private final VoucherTokenService tokenService;

    public ReverseRedeemUseCaseImpl(
            final VoucherGateway voucherGateway,
            final VoucherTokenService tokenService
    ) {
        this.voucherGateway = voucherGateway;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public ReverseRedeemOutput execute(final ReverseRedeemCommand command) {
        final var voucher = voucherGateway.findByDisplayCode(command.displayCode())
                .orElseThrow(() -> NotFoundException.with(new DomainError("Voucher not found with the provided publicToken")));

        final var refId = LedgerEntryId.from(command.refLedgerEntryId());

        final var reversalEntry = voucher.reversal(
                refId,
                MerchantId.from(command.merchantId()),
                command.idempotencyKey()
        );

        voucherGateway.update(voucher);

        return ReverseRedeemOutput.of(
                voucher.getId().getValue(),
                reversalEntry.id().getValue(),
                voucher.balanceCents()
        );
    }
}