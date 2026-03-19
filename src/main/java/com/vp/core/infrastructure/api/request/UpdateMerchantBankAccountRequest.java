package com.vp.core.infrastructure.api.request;

public record UpdateMerchantBankAccountRequest(
        String bankCode,
        String bankName,
        String branch,
        String accountNumber,
        String accountDigit,
        String accountType,
        String holderName,
        String holderDocument,
        String pixKeyType,
        String pixKeyValue
) {
}
