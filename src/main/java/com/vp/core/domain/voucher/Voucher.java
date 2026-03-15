package com.vp.core.domain.voucher;

import com.vp.core.domain.AggregateRoot;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.utils.InstantUtils;
import com.vp.core.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.*;

/**
 * Voucher Aggregate Root (ledger-first)
 *
 * - Voucher não guarda valor inicial nem saldo.
 * - Saldo é consequência do ledger.
 * - Regra: apenas 1 ISSUE por voucher.
 */
public class Voucher extends AggregateRoot<VoucherId> {

    private final CampaignId campaignId;

    private String tokenHash;
    private int tokenVersion;
    private String displayCode;

    private VoucherStatus status;
    private Instant expiresAt;
    private Instant issuedAt;

    private final List<LedgerEntry> ledger = new ArrayList<>();
    private long version;

    private Voucher(
            final VoucherId id,
            final CampaignId campaignId,
            final String tokenHash,
            final int tokenVersion,
            final String displayCode,
            final VoucherStatus status,
            final Instant expiresAt,
            final Instant issuedAt,
            final List<LedgerEntry> ledger
    ) {
        super(id);
        this.campaignId = campaignId;
        this.tokenHash = tokenHash;
        this.tokenVersion = tokenVersion;
        this.displayCode = displayCode;
        this.status = status;
        this.expiresAt = expiresAt;
        this.issuedAt = issuedAt;

        if (ledger != null) {
            this.ledger.addAll(ledger);
        }
    }

    private Voucher(
            final VoucherId id,
            final CampaignId campaignId,
            final String tokenHash,
            final int tokenVersion,
            final String displayCode,
            final VoucherStatus status,
            final Instant expiresAt,
            final List<LedgerEntry> ledger,
            final Instant createdAt,
            final Instant updatedAt,
            final long version,
            final Instant issuedAt
    ) {
        super(id, createdAt, updatedAt);

        this.campaignId = campaignId;
        this.tokenHash = tokenHash;
        this.tokenVersion = tokenVersion;
        this.displayCode = displayCode;
        this.status = status;
        this.expiresAt = expiresAt;
        this.version = version;
        this.issuedAt = issuedAt;

        if (ledger != null) {
            this.ledger.addAll(ledger);
        }
    }

    public static Voucher create(
            final CampaignId campaignId,
            final String tokenHash,
            final int tokenVersion,
            final String displayCode,
            final Instant expiresAt
    ) {
        final var id = VoucherId.newId();
        return new Voucher(
                id,
                campaignId,
                tokenHash,
                tokenVersion,
                displayCode,
                VoucherStatus.DRAFT,
                expiresAt,
                null,
                Collections.emptyList()
        );
    }

    public static Voucher with(
            final VoucherId id,
            final CampaignId campaignId,
            final String tokenHash,
            final int tokenVersion,
            final String displayCode,
            final VoucherStatus status,
            final Instant expiresAt,
            final List<LedgerEntry> ledger,
            final Instant createdAt,
            final Instant updatedAt,
            final long version,
            final Instant issuedAt
    ) {
        return new Voucher(
                id,
                campaignId,
                tokenHash,
                tokenVersion,
                displayCode,
                status,
                expiresAt,
                ledger,
                createdAt,
                updatedAt,
                version,
                issuedAt
        );
    }

    public void issue(final long amountCents, final String idempotencyKey) {
        if (hasIssue()) {
            throw new IllegalStateException("Voucher already has an ISSUE entry");
        }
        final LedgerEntry entry = LedgerEntry.issue(getId(), amountCents, idempotencyKey);

        this.ledger.add(entry);
        this.status = VoucherStatus.ACTIVE;
        this.issuedAt = InstantUtils.now();
        touch();
    }

    public LedgerEntry redeem(
            final long amountCents,
            final MerchantId merchantId,
            final String idempotencyKey
    ) {
        final LedgerEntry entry = LedgerEntry.redeem(getId(), amountCents, merchantId, idempotencyKey);
        ledger.add(entry);
        touch();
        return entry;
    }

    public Optional<LedgerEntry> findLedgerEntry(final LedgerEntryId id) {
        return this.ledger.stream()
                .filter(entry -> entry.id().equals(id))
                .findFirst();
    }

    public LedgerEntry reversal(
            final LedgerEntryId refLedgerEntryId,
            final MerchantId merchantId,
            final String idempotencyKey
    ) {
        final var refEntry = findLedgerEntry(refLedgerEntryId)
                .orElseThrow(() -> new IllegalStateException("Ledger entry not found"));

        if (refEntry.type() != LedgerEntryType.REDEEM) {
            throw new IllegalStateException("Only REDEEM can be reversed");
        }

        if (hasReversalFor(refLedgerEntryId)) {
            throw new IllegalStateException("Redeem already reversed");
        }

        final LedgerEntry entry = LedgerEntry.reversal(getId(), refEntry.amountCents(), merchantId, refLedgerEntryId, idempotencyKey);
        ledger.add(entry);
        touch();
        return entry;
    }

    public boolean hasReversalFor(final LedgerEntryId refId) {
        return this.ledger.stream()
                .anyMatch(e ->
                        e.type() == LedgerEntryType.REVERSAL &&
                                refId.equals(e.refLedgerEntryId())
                );
    }

    public CampaignId campaignId() {
        return campaignId;
    }

    public String tokenHash() {
        return tokenHash;
    }

    public int tokenVersion() {
        return tokenVersion;
    }

    public VoucherStatus status() {
        return status;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public Instant issuedAt() {
        return issuedAt;
    }

    public String displayCode() {
    return displayCode;
    }

    public List<LedgerEntry> ledger() {
        return Collections.unmodifiableList(ledger);
    }

    public boolean hasIssue() {
        return ledger.stream().anyMatch(e -> e.type() == LedgerEntryType.ISSUE);
    }

    public long balanceCents() {
        return ledger.stream().mapToLong(LedgerEntry::signedAmountCents).sum();
    }

    public long getVersion() {
        return version;
    }

    @Override
    public void validate(final ValidationHandler handler) {
        // sem validações por enquanto
    }
}
