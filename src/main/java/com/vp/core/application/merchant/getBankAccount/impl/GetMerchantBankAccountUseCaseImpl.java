package com.vp.core.application.merchant.getBankAccount.impl;

import com.vp.core.application.merchant.getBankAccount.GetMerchantBankAccountCommand;
import com.vp.core.application.merchant.getBankAccount.GetMerchantBankAccountOutput;
import com.vp.core.application.merchant.getBankAccount.GetMerchantBankAccountUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.PixKey;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetMerchantBankAccountUseCaseImpl extends GetMerchantBankAccountUseCase {

    private final MerchantGateway merchantGateway;

    public GetMerchantBankAccountUseCaseImpl(final MerchantGateway merchantGateway) {
        this.merchantGateway = merchantGateway;
    }

    @Override
    public Optional<GetMerchantBankAccountOutput> execute(final GetMerchantBankAccountCommand command) {
        final var merchantId = MerchantId.from(command.merchantId());
        final var tenantId = TenantId.from(command.tenantId());

        final var merchant = merchantGateway.findByIdAndTenantId(merchantId, tenantId)
                .orElseThrow(() -> NotFoundException.with(Merchant.class, merchantId));

        final var bankAccount = merchant.getBankAccount();
        if (bankAccount == null) {
            return Optional.empty();
        }

        final var pixKey = bankAccount.getPixKey();
        final String pixKeyType = pixKey != null && pixKey.getType() != null ? pixKey.getType().name() : null;
        final String pixKeyValue = pixKey != null ? pixKey.getValue() : null;
        final String holderDocument = bankAccount.getHolderDocument() != null
                ? bankAccount.getHolderDocument().getValue()
                : null;
        final String accountType = bankAccount.getAccountType() != null ? bankAccount.getAccountType().name() : null;

        return Optional.of(GetMerchantBankAccountOutput.of(
                merchant.getId().getValue(),
                bankAccount.getBankCode(),
                bankAccount.getBankName(),
                bankAccount.getBranch(),
                bankAccount.getAccountNumber(),
                bankAccount.getAccountDigit(),
                accountType,
                bankAccount.getHolderName(),
                holderDocument,
                pixKeyType,
                pixKeyValue
        ));
    }
}
