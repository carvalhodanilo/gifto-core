package com.vp.core.application.tenant.getBankAccount.impl;

import com.vp.core.application.tenant.getBankAccount.GetTenantBankAccountCommand;
import com.vp.core.application.tenant.getBankAccount.GetTenantBankAccountOutput;
import com.vp.core.application.tenant.getBankAccount.GetTenantBankAccountUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.PixKey;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetTenantBankAccountUseCaseImpl extends GetTenantBankAccountUseCase {

    private final TenantGateway tenantGateway;

    public GetTenantBankAccountUseCaseImpl(final TenantGateway tenantGateway) {
        this.tenantGateway = tenantGateway;
    }

    @Override
    public Optional<GetTenantBankAccountOutput> execute(final GetTenantBankAccountCommand command) {
        final var tenantId = TenantId.from(command.tenantId());

        final var tenant = tenantGateway.findById(tenantId)
                .orElseThrow(() -> NotFoundException.with(Tenant.class, tenantId));

        final var bankAccount = tenant.getBankAccount();
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

        return Optional.of(GetTenantBankAccountOutput.of(
                tenant.getId().getValue(),
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
