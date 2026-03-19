package com.vp.core.application.merchant.getBankAccount;

public record GetMerchantBankAccountOutput(
        String merchantId,
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

    public static GetMerchantBankAccountOutput of(
            final String merchantId,
            final String bankCode,
            final String bankName,
            final String branch,
            final String accountNumber,
            final String accountDigit,
            final String accountType,
            final String holderName,
            final String holderDocument,
            final String pixKeyType,
            final String pixKeyValue
    ) {
        return new GetMerchantBankAccountOutput(
                merchantId,
                bankCode,
                bankName,
                branch,
                accountNumber,
                accountDigit,
                accountType,
                holderName,
                holderDocument,
                pixKeyType,
                pixKeyValue
        );
    }
}
