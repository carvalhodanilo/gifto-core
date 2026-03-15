package com.vp.core.infrastructure.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BankAccountEmbeddable {

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_branch")
    private String branch;

    @Column(name = "bank_account_number")
    private String accountNumber;

    @Column(name = "bank_account_digit")
    private String accountDigit;

    @Column(name = "bank_account_type")
    private String accountType;

    @Column(name = "bank_holder_name")
    private String holderName;

    @Column(name = "bank_holder_document_value")
    private String holderDocumentValue;

    @Column(name = "bank_pix_key_type")
    private String pixKeyType;

    @Column(name = "bank_pix_key_value")
    private String pixKeyValue;

    protected BankAccountEmbeddable() {
    }

    public static BankAccountEmbeddable of(
            final String bankCode,
            final String bankName,
            final String branch,
            final String accountNumber,
            final String accountDigit,
            final String accountType,
            final String holderName,
            final String holderDocumentValue,
            final String pixKeyType,
            final String pixKeyValue
    ) {
        final var e = new BankAccountEmbeddable();
        e.bankCode = bankCode;
        e.bankName = bankName;
        e.branch = branch;
        e.accountNumber = accountNumber;
        e.accountDigit = accountDigit;
        e.accountType = accountType;
        e.holderName = holderName;
        e.holderDocumentValue = holderDocumentValue;
        e.pixKeyType = pixKeyType;
        e.pixKeyValue = pixKeyValue;
        return e;
    }

    public String getBankCode() { return bankCode; }
    public String getBankName() { return bankName; }
    public String getBranch() { return branch; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountDigit() { return accountDigit; }
    public String getAccountType() { return accountType; }
    public String getHolderName() { return holderName; }
    public String getHolderDocumentValue() { return holderDocumentValue; }
    public String getPixKeyType() { return pixKeyType; }
    public String getPixKeyValue() { return pixKeyValue; }
}