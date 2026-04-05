package com.vp.core.application.tenant.updateBankAccount;

import com.vp.core.domain.valueObjects.PixKey;

public record UpdateTenantBankAccountCommand(
        String tenantId,
        String bankCode,
        String bankName,
        String branch,
        String accountNumber,
        String accountDigit,
        String accountType,
        String holderName,
        String holderDocument,
        PixKey.PixKeyType pixKeyType,
        String pixKeyValue
) {
}
