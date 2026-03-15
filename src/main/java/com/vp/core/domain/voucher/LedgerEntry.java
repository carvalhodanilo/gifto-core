package com.vp.core.domain.voucher;

import com.vp.core.domain.Entity;
import com.vp.core.domain.ValueObject;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.utils.InstantUtils;
import com.vp.core.domain.validation.ValidationHandler;

import java.time.Instant;

public class LedgerEntry extends Entity<LedgerEntryId> {

    private final VoucherId voucherId;
    private final LedgerEntryType type;
    private final long amountCents;
    private final MerchantId merchantId;
    private final LedgerEntryId refLedgerEntryId;
    private final String idempotencyKey;

    private LedgerEntry(
            final LedgerEntryId id,
            final VoucherId voucherId,
            final LedgerEntryType type,
            final long amountCents,
            final MerchantId merchantId,
            final LedgerEntryId refLedgerEntryId,
            final String idempotencyKey,
            final Instant createdAt
    ) {
        super(id);
        this.voucherId = voucherId;
        this.type = type;
        this.amountCents = amountCents;
        this.merchantId = merchantId;
        this.refLedgerEntryId = refLedgerEntryId;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = createdAt;
    }

    public static LedgerEntry with(
            final LedgerEntryId id,
            final VoucherId voucherId,
            final LedgerEntryType type,
            final long amountCents,
            final MerchantId merchantId,
            final LedgerEntryId refLedgerEntryId,
            final String idempotencyKey,
            final Instant createdAt
    ) {
        return new LedgerEntry(id, voucherId, type, amountCents, merchantId, refLedgerEntryId, idempotencyKey, createdAt);
    }

    public static LedgerEntry issue(
            final VoucherId voucherId,
            final long amountCents,
            final String idempotencyKey
    ) {
        final var id = LedgerEntryId.newId();
        return new LedgerEntry(id, voucherId, LedgerEntryType.ISSUE, amountCents, null, null, idempotencyKey, InstantUtils.now());
    }

    public static LedgerEntry redeem(
            final VoucherId voucherId,
            final long amountCents,
            final MerchantId merchantId,
            final String idempotencyKey
    ) {
        final var id = LedgerEntryId.newId();
        return new LedgerEntry(id, voucherId, LedgerEntryType.REDEEM, amountCents, merchantId, null, idempotencyKey, InstantUtils.now());
    }

    public static LedgerEntry reversal(
            final VoucherId voucherId,
            final long amountCents,
            final MerchantId merchantId,
            final LedgerEntryId refLedgerEntryId,
            final String idempotencyKey
    ) {
        final var id = LedgerEntryId.newId();
        return new LedgerEntry(id, voucherId, LedgerEntryType.REVERSAL, amountCents, merchantId, refLedgerEntryId, idempotencyKey, InstantUtils.now());
    }

    public long signedAmountCents() {
        return type == LedgerEntryType.REDEEM ? -amountCents : amountCents;
    }

    public LedgerEntryId id() { return id; }
    public VoucherId voucherId() { return voucherId; }
    public LedgerEntryType type() { return type; }
    public long amountCents() { return amountCents; }
    public MerchantId merchantId() { return merchantId; }
    public LedgerEntryId refLedgerEntryId() { return refLedgerEntryId; }
    public String idempotencyKey() { return idempotencyKey; }
    public Instant createdAt() { return createdAt; }

    @Override
    public void validate(ValidationHandler handler) {

    }
}
