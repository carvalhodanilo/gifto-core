package com.vp.core.application.merchant.update.impl;

import com.vp.core.application.merchant.update.UpdateMerchantCommand;
import com.vp.core.application.merchant.update.UpdateMerchantOutput;
import com.vp.core.application.merchant.update.UpdateMerchantUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.valueObjects.Email;
import org.springframework.transaction.annotation.Transactional;

public class UpdateMerchantUseCaseImpl extends UpdateMerchantUseCase {

    private final MerchantGateway merchantGateway;

    public UpdateMerchantUseCaseImpl(final MerchantGateway merchantGateway) {
        this.merchantGateway = merchantGateway;
    }

    @Override
    @Transactional
    public UpdateMerchantOutput execute(final UpdateMerchantCommand command) {
        final var merchantId = MerchantId.from(command.merchantId());

        final var merchant = merchantGateway.findById(merchantId)
                .orElseThrow(() -> NotFoundException.with(Merchant.class, merchantId));

        merchant.updateProfile(
                command.name(),
                command.fantasyName(),
                command.phone1(),
                command.phone2(),
                Email.with(command.email()),
                command.url()
        );

        merchantGateway.update(merchant);
        return UpdateMerchantOutput.of(merchantId.getValue());
    }
}
