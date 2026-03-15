package com.vp.core.domain.settlement;

import com.vp.core.domain.Entity;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.utils.InstantUtils;
import com.vp.core.domain.validation.ValidationHandler;

import java.time.Instant;

public class SettlementEntry extends Entity<SettlementEntryId> {

    private final MerchantId merchantId;

    private final long grossCents;
    private final long reversalsCents;
    private final long feesCents;
    private final long netCents;

    private SettlementEntryStatus status;
    private Instant paidAt;
    private String paymentRef;

    private SettlementEntry(
            final SettlementEntryId id,
            final MerchantId merchantId,
            final long grossCents,
            final long reversalsCents,
            final long feesCents,
            final long netCents,
            final SettlementEntryStatus status,
            final Instant paidAt,
            final String paymentRef
    ) {
        super(id);
        this.merchantId = merchantId;
        this.grossCents = grossCents;
        this.reversalsCents = reversalsCents;
        this.feesCents = feesCents;
        this.netCents = netCents;
        this.status = status;
        this.paidAt = paidAt;
        this.paymentRef = paymentRef;
    }

    public static SettlementEntry create(
            final MerchantId merchantId,
            final long grossCents,
            final long reversalsCents,
            final long feesCents
    ) {
        final long net = grossCents - reversalsCents - feesCents;

        return new SettlementEntry(
                SettlementEntryId.newId(),
                merchantId,
                grossCents,
                reversalsCents,
                feesCents,
                net,
                SettlementEntryStatus.PENDING,
                null,
                null
        );
    }

    /** Reconstitution from persistence. */
    public static SettlementEntry with(
            final SettlementEntryId id,
            final MerchantId merchantId,
            final long grossCents,
            final long reversalsCents,
            final long feesCents,
            final long netCents,
            final SettlementEntryStatus status,
            final Instant paidAt,
            final String paymentRef
    ) {
        return new SettlementEntry(
                id,
                merchantId,
                grossCents,
                reversalsCents,
                feesCents,
                netCents,
                status,
                paidAt,
                paymentRef
        );
    }

    public void markPaid(final String paymentRef) {
        if (this.status == SettlementEntryStatus.PAID) {
            return;
        }

        this.status = SettlementEntryStatus.PAID;
        this.paidAt = InstantUtils.now();
        this.paymentRef = paymentRef;
    }

    public MerchantId merchantId() { return merchantId; }
    public long grossCents() { return grossCents; }
    public long reversalsCents() { return reversalsCents; }
    public long feesCents() { return feesCents; }
    public long netCents() { return netCents; }
    public SettlementEntryStatus status() { return status; }
    public Instant paidAt() { return paidAt; }
    public String paymentRef() { return paymentRef; }

    @Override
    public void validate(ValidationHandler handler) {
        // MVP
    }
}