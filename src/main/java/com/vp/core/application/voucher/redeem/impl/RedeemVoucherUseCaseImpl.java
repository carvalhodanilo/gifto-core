package com.vp.core.application.voucher.redeem.impl;

import com.vp.core.application.voucher.redeem.RedeemVoucherCommand;
import com.vp.core.application.voucher.redeem.RedeemVoucherOutput;
import com.vp.core.application.voucher.redeem.RedeemVoucherUseCase;
import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.gateway.VoucherGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.validation.DomainError;
import com.vp.core.domain.voucher.VoucherStatus;
import jakarta.persistence.OptimisticLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RedeemVoucherUseCaseImpl extends RedeemVoucherUseCase {

    private final VoucherGateway voucherGateway;
    private final CampaignGateway campaignGateway;
    private final MerchantGateway merchantGateway;

    public RedeemVoucherUseCaseImpl(
            final VoucherGateway voucherGateway,
            final CampaignGateway campaignGateway,
            final MerchantGateway merchantGateway
    ) {
        this.voucherGateway = voucherGateway;
        this.campaignGateway = campaignGateway;
        this.merchantGateway = merchantGateway;
    }

    @Override
    @Transactional
    public RedeemVoucherOutput execute(final RedeemVoucherCommand command) {
        final var tenantId = TenantId.from(command.tenantId());
        final var merchantId = MerchantId.from(command.merchantId());

        final var voucher = voucherGateway.findByDisplayCode(command.displayCode())
                .orElseThrow(() -> NotFoundException.with(
                        new DomainError("Voucher not found with the provided Display Code")
                ));

        final var campaignId = CampaignId.from(voucher.campaignId().getValue());
        final Campaign campaign = campaignGateway.findByTenantIdAndId(tenantId, campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId.getValue()));
        campaign.ensureActive();

        final Merchant merchant = merchantGateway.findByIdAndTenantId(merchantId, tenantId)
                .orElseThrow(() -> new RuntimeException("Merchant not found: " + merchantId.getValue()));

        if (!merchant.isActiveMemberOf(campaign.networkId())) {
            throw new RuntimeException("Merchant is not member of campaign network");
        }

        final var existing = voucher.ledger().stream()
                .filter(e -> e.idempotencyKey().equals(command.idempotencyKey()))
                .findFirst();

        if (existing.isPresent()) {
            final var e = existing.get();
            return RedeemVoucherOutput.of(
                    voucher.getId().getValue(),
                    e.id().getValue(),
                    voucher.balanceCents(),
                    e.createdAt()
            );
        }

        if (voucher.status() != VoucherStatus.ACTIVE) {
            throw new IllegalStateException("Voucher is not active");
        }

        final var now = Instant.now();
        if (voucher.expiresAt() != null && now.isAfter(voucher.expiresAt())) {
            throw new IllegalStateException("Voucher is expired");
        }

        final var currentBalance = voucher.balanceCents();
        if (command.amountCents() > currentBalance) {
            throw new IllegalStateException("Insufficient balance");
        }

        final var entry = voucher.redeem(
                command.amountCents(),
                MerchantId.from(command.merchantId()),
                command.idempotencyKey()
        );

        try {
            voucherGateway.update(voucher);
        } catch (OptimisticLockException | OptimisticLockingFailureException ex) {
            throw new IllegalStateException("Concurrent redeem detected, please retry", ex);
        }

        return RedeemVoucherOutput.of(
                voucher.getId().getValue(),
                entry.id().getValue(),
                voucher.balanceCents(),
                entry.createdAt()
        );
    }
}