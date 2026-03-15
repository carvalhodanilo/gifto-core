package com.vp.core.application.voucher.issue.impl;

import com.vp.core.application.VoucherDisplayCodeService;
import com.vp.core.application.VoucherTokenService;
import com.vp.core.application.voucher.issue.IssueVoucherCommand;
import com.vp.core.application.voucher.issue.IssueVoucherOutput;
import com.vp.core.application.voucher.issue.IssueVoucherUseCase;
import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.gateway.VoucherGateway;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.voucher.Voucher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class IssueVoucherUseCaseImpl extends IssueVoucherUseCase {

    private final CampaignGateway campaignGateway;
    private final VoucherGateway voucherGateway;
    private final VoucherTokenService tokenService;
    private final VoucherDisplayCodeService voucherDisplayCodeService;

    public IssueVoucherUseCaseImpl(
            final CampaignGateway campaignGateway,
            final VoucherGateway voucherGateway,
            final VoucherTokenService tokenService,
            final VoucherDisplayCodeService voucherDisplayCodeService
    ) {
        this.campaignGateway = campaignGateway;
        this.voucherGateway = voucherGateway;
        this.tokenService = tokenService;
        this.voucherDisplayCodeService = voucherDisplayCodeService;
    }

    @Override
    @Transactional
    public IssueVoucherOutput execute(final IssueVoucherCommand command) {
        final var tenantId = TenantId.from(command.tenantId());
        final var campaignId = CampaignId.from(command.campaignId());

        final var campaign = campaignGateway.findByTenantIdAndId(tenantId, campaignId)
                .orElseThrow(() -> NotFoundException.with(Campaign.class, campaignId));
        campaign.ensureActive();

        final var tokenData = tokenService.generate();
        final var expiresAt = resolveExpiresAt(campaign);
        final var displayCode = voucherDisplayCodeService.generate();

        final var voucher = Voucher.create(
                campaignId,
                tokenData.tokenHash(),
                tokenData.tokenVersion(),
                displayCode,
                expiresAt
        );

        voucher.issue(
                command.amountCents(),
                command.idempotencyKey()
        );
        voucherGateway.create(voucher);

        return IssueVoucherOutput.of(
                voucher.getId().getValue(),
                tokenData.publicToken(),
                displayCode,
                expiresAt
        );
    }

    private Instant resolveExpiresAt(final Campaign campaign) {
        return Instant.now().plus(campaign.expirationDays(), ChronoUnit.DAYS);
    }
}