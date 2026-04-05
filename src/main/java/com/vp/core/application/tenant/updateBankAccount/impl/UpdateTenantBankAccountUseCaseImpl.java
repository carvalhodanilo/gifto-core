package com.vp.core.application.tenant.updateBankAccount.impl;

import com.vp.core.application.tenant.updateBankAccount.UpdateTenantBankAccountCommand;
import com.vp.core.application.tenant.updateBankAccount.UpdateTenantBankAccountOutput;
import com.vp.core.application.tenant.updateBankAccount.UpdateTenantBankAccountUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.AccountType;
import com.vp.core.domain.valueObjects.BankAccount;
import com.vp.core.domain.valueObjects.Document;
import com.vp.core.domain.valueObjects.PixKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTenantBankAccountUseCaseImpl extends UpdateTenantBankAccountUseCase {

    private final TenantGateway tenantGateway;

    public UpdateTenantBankAccountUseCaseImpl(final TenantGateway tenantGateway) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    @Transactional
    public UpdateTenantBankAccountOutput execute(final UpdateTenantBankAccountCommand command) {
        final var tenantId = TenantId.from(command.tenantId());

        final var tenant = tenantGateway.findById(tenantId)
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

        tenant.updateBankAccount(bankAccount);
        tenantGateway.update(tenant);
        return UpdateTenantBankAccountOutput.of(tenantId.getValue());
    }

    private PixKey buildPixKey(final PixKey.PixKeyType type, final String value) {
        if (type == null || value == null) {
            return null;
        }
        return PixKey.of(type, value);
    }
}
