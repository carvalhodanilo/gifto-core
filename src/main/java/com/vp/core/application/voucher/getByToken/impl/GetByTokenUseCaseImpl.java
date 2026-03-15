package com.vp.core.application.voucher.getByToken.impl;

import com.vp.core.application.VoucherTokenService;
import com.vp.core.application.voucher.getByToken.GetByTokenCommand;
import com.vp.core.application.voucher.getByToken.GetByTokenOutput;
import com.vp.core.application.voucher.getByToken.GetByTokenUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.VoucherGateway;
import com.vp.core.domain.validation.DomainError;
import com.vp.core.domain.voucher.Voucher;
import org.springframework.stereotype.Service;

@Service
public class GetByTokenUseCaseImpl extends GetByTokenUseCase {

    private final VoucherGateway voucherGateway;
    private final VoucherTokenService tokenService;

    public GetByTokenUseCaseImpl(
            final VoucherGateway voucherGateway,
            final VoucherTokenService tokenService
    ) {
        this.voucherGateway = voucherGateway;
        this.tokenService = tokenService;
    }

    @Override
    public GetByTokenOutput execute(final GetByTokenCommand command) {
        final var tokenHash = tokenService.hash(command.token());

        final var voucher = voucherGateway.findByTokenHash(tokenHash)
                .orElseThrow(() -> NotFoundException.with(new DomainError("Voucher not found with the provided publicToken")));

        final var balance = voucher.balanceCents();

        return GetByTokenOutput.of(
                voucher.getId().getValue(),
                voucher.campaignId().getValue(),
                voucher.status().name(),
                voucher.expiresAt(),
                balance
        );
    }
}