package com.vp.core.application.tenant.updateAccount;

import com.vp.core.domain.valueObjects.PixKey;
import com.vp.core.domain.valueObjects.URL;

import java.util.Set;

public record UpdateTenantBankAccountCommand(
        String tenantId,
        String bankCode,
        String bankName,
        String branch,
        String accountNumber,
        String accountDigit,
        String accountType,     // vem como String (CHECKING/SAVINGS/PAYMENT)
        String holderName,
        String holderDocument,
        PixKey.PixKeyType pixKeyType,      // opcional (ex: "CPF", "CNPJ", "EMAIL", "PHONE", "EVP")
        String pixKeyValue      // opcional
) {
}