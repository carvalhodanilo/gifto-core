package com.vp.core.domain.valueObjects;

import com.vp.core.domain.ValueObject;
import com.vp.core.domain.validation.ValidationHandler;

import java.util.Objects;

public class BankAccount extends ValueObject {
    private final String bankCode;          // ex: "001"
    private final String bankName;          // opcional (pode vir de catálogo)
    private final String branch;            // agência
    private final String accountNumber;     // conta
    private final String accountDigit;      // dígito (pode ser null dependendo do banco)
    private final AccountType accountType;  // CHECKING/SAVINGS/PAYMENT
    private final String holderName;
    private final Document holderDocument;
    private final PixKey pixKey;

    private BankAccount(
            final String bankCode,
            final String bankName,
            final String branch,
            final String accountNumber,
            final String accountDigit,
            final AccountType accountType,
            final String holderName,
            final Document holderDocument,
            final PixKey pixKey
    ) {
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.branch = branch;
        this.accountNumber = accountNumber;
        this.accountDigit = accountDigit;
        this.accountType = accountType;
        this.holderName = holderName;
        this.holderDocument = holderDocument;
        this.pixKey = pixKey;
    }

    public static BankAccount of(
            final String bankCode,
            final String bankName,
            final String branch,
            final String accountNumber,
            final String accountDigit,
            final AccountType accountType,
            final String holderName,
            final Document holderDocument,
            final PixKey pixKey
    ) {
        return new BankAccount(
                bankCode,
                bankName,
                branch,
                accountNumber,
                accountDigit,
                accountType,
                holderName,
                holderDocument,
                pixKey
        );
    }

    public static BankAccount empty() {
        return null;
    }

    public void validate(final ValidationHandler handler) {
    }

    public String getBankCode() { return bankCode; }
    public String getBankName() { return bankName; }
    public String getBranch() { return branch; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountDigit() { return accountDigit; }
    public AccountType getAccountType() { return accountType; }
    public String getHolderName() { return holderName; }
    public Document getHolderDocument() { return holderDocument; }
    public PixKey getPixKey() { return pixKey; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccount that)) return false;
        return Objects.equals(bankCode, that.bankCode)
                && Objects.equals(branch, that.branch)
                && Objects.equals(accountNumber, that.accountNumber)
                && Objects.equals(accountDigit, that.accountDigit)
                && accountType == that.accountType
                && Objects.equals(holderDocument, that.holderDocument)
                && Objects.equals(pixKey, that.pixKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankCode, branch, accountNumber, accountDigit, accountType, holderDocument, pixKey);
    }
}
