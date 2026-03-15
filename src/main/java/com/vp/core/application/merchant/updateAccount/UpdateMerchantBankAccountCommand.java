package com.vp.core.application.merchant.updateAccount;

import com.vp.core.domain.valueObjects.PixKey;

public record UpdateMerchantBankAccountCommand(
        String tenantId,
        String merchantId,
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