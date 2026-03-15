package com.vp.core.application.merchant.updateAccount.impl;

import com.vp.core.application.merchant.updateAccount.UpdateMerchantBankAccountCommand;
import com.vp.core.application.merchant.updateAccount.UpdateMerchantBankAccountOutput;
import com.vp.core.application.merchant.updateAccount.UpdateMerchantBankAccountUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.AccountType;
import com.vp.core.domain.valueObjects.BankAccount;
import com.vp.core.domain.valueObjects.Document;
import com.vp.core.domain.valueObjects.PixKey;
import org.springframework.transaction.annotation.Transactional;

public class UpdateMerchantBankAccountUseCaseImpl extends UpdateMerchantBankAccountUseCase {

    private final MerchantGateway tenantGateway;

    public UpdateMerchantBankAccountUseCaseImpl(final MerchantGateway tenantGateway) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    @Transactional
    public UpdateMerchantBankAccountOutput execute(final UpdateMerchantBankAccountCommand command) {
        final var merchantId = MerchantId.from(command.merchantId());
        final var tenantId = TenantId.from(command.tenantId());

        final var merchant = tenantGateway.findByIdAndTenantId(merchantId, tenantId)
                .orElseThrow(() -> NotFoundException.with(Tenant.class, tenantId));

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
        tenantGateway.update(merchant);
        return UpdateMerchantBankAccountOutput.of(merchantId.getValue(), tenantId.getValue());
    }

    private PixKey buildPixKey(final PixKey.PixKeyType type, final String value) {
        if (type == null || value == null) {
            return null;
        }
        return PixKey.of(type, value);
    }
}