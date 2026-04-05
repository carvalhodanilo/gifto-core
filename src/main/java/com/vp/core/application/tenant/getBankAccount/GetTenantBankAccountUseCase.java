package com.vp.core.application.tenant.getBankAccount;

import com.vp.core.application.UseCase;

import java.util.Optional;

public abstract class GetTenantBankAccountUseCase
        extends UseCase<GetTenantBankAccountCommand, Optional<GetTenantBankAccountOutput>> {
}
