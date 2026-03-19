package com.vp.core.application.merchant.updateAccount.impl;

import com.vp.core.application.merchant.updateAccount.UpdateMerchantBankAccountCommand;
import com.vp.core.application.merchant.updateAccount.UpdateMerchantBankAccountOutput;
import com.vp.core.application.merchant.updateAccount.UpdateMerchantBankAccountUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.AccountType;
import com.vp.core.domain.valueObjects.BankAccount;
import com.vp.core.domain.valueObjects.Document;
import com.vp.core.domain.valueObjects.PixKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateMerchantBankAccountUseCaseImpl extends UpdateMerchantBankAccountUseCase {

    private final MerchantGateway merchantGateway;

    public UpdateMerchantBankAccountUseCaseImpl(final MerchantGateway merchantGateway) {
        this.merchantGateway = merchantGateway;
    }

    @Override
    @Transactional
    public UpdateMerchantBankAccountOutput execute(final UpdateMerchantBankAccountCommand command) {
        final var merchantId = MerchantId.from(command.merchantId());
        final var tenantId = TenantId.from(command.tenantId());

        final var merchant = merchantGateway.findByIdAndTenantId(merchantId, tenantId)
                .orElseThrow(() -> NotFoundException.with(Merchant.class, merchantId));

        final var accountType = AccountType.valueOf(command.accountType());
        final var pixKey = buildPixKey(command.pixKeyType(), command.pixKeyValue());
        final var bankAccount = BankAccount.of(
                command.bankCode(),
                command.bankName(),
                command.branch(),
                command.accountNumber(),
                command.accountDigit(),
                accountType,
                command.holderName(),
                Document.with(command.holderDocument()),
                pixKey
        );

        merchant.updateBankAccount(bankAccount);
        merchantGateway.update(merchant);
        return UpdateMerchantBankAccountOutput.of(merchantId.getValue(), tenantId.getValue());
    }

    private PixKey buildPixKey(final PixKey.PixKeyType type, final String value) {
        if (type == null || value == null) {
            return null;
        }
        return PixKey.of(type, value);
    }
}