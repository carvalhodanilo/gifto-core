package com.vp.core.application.voucher.getByDisplayCode.impl;

import com.vp.core.application.VoucherTokenService;
import com.vp.core.application.voucher.getByDisplayCode.GetByDisplayCodeCommand;
import com.vp.core.application.voucher.getByDisplayCode.GetByDisplayCodeOutput;
import com.vp.core.application.voucher.getByDisplayCode.GetByDisplayCodeUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.gateway.VoucherGateway;
import com.vp.core.domain.validation.DomainError;
import org.springframework.stereotype.Service;

@Service
public class GetByDisplayCodeUseCaseImpl extends GetByDisplayCodeUseCase {

    private final VoucherGateway voucherGateway;
    private final CampaignGateway campaignGateway;

    public GetByDisplayCodeUseCaseImpl(
            final VoucherGateway voucherGateway,
            final CampaignGateway campaignGateway
    ) {
        this.voucherGateway = voucherGateway;
        this.campaignGateway = campaignGateway;
    }

    @Override
    public GetByDisplayCodeOutput execute(final GetByDisplayCodeCommand command) {
        final var voucher = voucherGateway.findByDisplayCode(command.displayCode())
                .orElseThrow(() -> NotFoundException.with(new DomainError("Voucher not found with the provided publicToken")));

        final var campaign = campaignGateway.findById(voucher.campaignId())
                .orElseThrow(() -> new RuntimeException("Campaign not found: " + voucher.campaignId().getValue()));

        final var balance = voucher.balanceCents();

        return GetByDisplayCodeOutput.of(
                voucher.getId().getValue(),
                campaign.name(),
                voucher.displayCode(),
                voucher.status().name(),
                voucher.expiresAt(),
                balance
        );
    }
}