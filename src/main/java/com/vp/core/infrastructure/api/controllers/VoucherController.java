package com.vp.core.infrastructure.api.controllers;

import com.vp.core.application.voucher.getByDisplayCode.GetByDisplayCodeCommand;
import com.vp.core.application.voucher.getByDisplayCode.GetByDisplayCodeOutput;
import com.vp.core.application.voucher.getByDisplayCode.GetByDisplayCodeUseCase;
import com.vp.core.application.voucher.getByToken.GetByTokenCommand;
import com.vp.core.application.voucher.getByToken.GetByTokenOutput;
import com.vp.core.application.voucher.getByToken.GetByTokenUseCase;
import com.vp.core.application.voucher.getLegderEntriesByMerchant.ListLedgerEntriesByMerchantCommand;
import com.vp.core.application.voucher.getLegderEntriesByMerchant.ListLedgerEntriesByMerchantOutput;
import com.vp.core.application.voucher.getLegderEntriesByMerchant.impl.ListLedgerEntriesByMerchantUseCaseImpl;
import com.vp.core.application.voucher.issue.IssueVoucherCommand;
import com.vp.core.application.voucher.listByTenant.ListByTenantCommand;
import com.vp.core.application.voucher.listByTenant.ListByTenantOutput;
import com.vp.core.application.voucher.listByTenant.ListByTenantUseCase;
import com.vp.core.application.voucher.issue.IssueVoucherUseCase;
import com.vp.core.application.voucher.redeem.RedeemVoucherCommand;
import com.vp.core.application.voucher.redeem.RedeemVoucherUseCase;
import com.vp.core.application.voucher.reversal.ReverseRedeemCommand;
import com.vp.core.application.voucher.reversal.ReverseRedeemUseCase;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.pagination.SearchVoucherQuery;
import com.vp.core.application.security.AccessScopeService;
import com.vp.core.application.security.CurrentUserProvider;
import com.vp.core.infrastructure.api.request.IssueVoucherRequest;
import com.vp.core.infrastructure.api.request.RedeemVoucherRequest;
import com.vp.core.infrastructure.api.request.ReverseVoucherRequest;
import com.vp.core.infrastructure.api.response.IssueVoucherResponse;
import com.vp.core.infrastructure.api.response.RedeemVoucherResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/vouchers")
public class VoucherController {

    private final IssueVoucherUseCase issueVoucherUseCase;
    private final RedeemVoucherUseCase redeemVoucherUseCase;
    private final GetByTokenUseCase getByTokenUseCase;
    private final GetByDisplayCodeUseCase getByDisplayCodeUseCase;
    private final ReverseRedeemUseCase reverseRedeemUseCase;

    private final ListLedgerEntriesByMerchantUseCaseImpl listLedgerEntriesByMerchantUseCase;
    private final ListByTenantUseCase listByTenantUseCase;
    private final CurrentUserProvider currentUserProvider;
    private final AccessScopeService accessScopeService;

    public VoucherController(
            final IssueVoucherUseCase issueVoucherUseCase,
            final RedeemVoucherUseCase redeemVoucherUseCase,
            final GetByTokenUseCase getByTokenUseCase,
            final ReverseRedeemUseCase reverseRedeemUseCase,
            final GetByDisplayCodeUseCase getByDisplayCodeUseCase,
            final ListLedgerEntriesByMerchantUseCaseImpl listLedgerEntriesByMerchantUseCase,
            final ListByTenantUseCase listByTenantUseCase,
            final CurrentUserProvider currentUserProvider,
            final AccessScopeService accessScopeService
    ) {
        this.issueVoucherUseCase = issueVoucherUseCase;
        this.redeemVoucherUseCase = redeemVoucherUseCase;
        this.getByTokenUseCase = getByTokenUseCase;
        this.reverseRedeemUseCase = reverseRedeemUseCase;
        this.getByDisplayCodeUseCase = getByDisplayCodeUseCase;
        this.listLedgerEntriesByMerchantUseCase = listLedgerEntriesByMerchantUseCase;
        this.listByTenantUseCase = listByTenantUseCase;
        this.currentUserProvider = currentUserProvider;
        this.accessScopeService = accessScopeService;
    }

    @PostMapping("/issue")
    // [tenant_admin, tenant_operator]
    // remover tenant id do body e colocar via token
    @PreAuthorize("hasAnyRole('tenant_admin', 'tenant_operator')")
    public ResponseEntity<IssueVoucherResponse> issue(
            @RequestHeader(value = "Idempotency-Key", required = false) final String idempotencyKeyHeader,
            @RequestBody @Valid final IssueVoucherRequest body
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        final var idempotencyKey =
                (idempotencyKeyHeader != null && !idempotencyKeyHeader.isBlank())
                        ? idempotencyKeyHeader
                        : (body.idempotencyKey() != null && !body.idempotencyKey().isBlank())
                        ? body.idempotencyKey()
                        : UUID.randomUUID().toString();

        final var out = issueVoucherUseCase.execute(new IssueVoucherCommand(
                tenantId,
                body.campaignId(),
                body.amountCents(),
                idempotencyKey,
                body.buyerName().trim(),
                body.buyerPhone().trim()
        ));

        final var response = IssueVoucherResponse.of(
                out.voucherId(),
                out.token(),
                out.displayCode(),
                out.expiresAt()
        );

        return ResponseEntity
                .created(URI.create("/v1/vouchers/" + out.voucherId()))
                .body(response);
    }

    @PostMapping("/redeem")
    // [merchant_admin, merchant_operator]
    // remover tenant id do body e colocar via token
    @PreAuthorize("hasAnyRole('merchant_admin', 'merchant_operator')")
    public ResponseEntity<RedeemVoucherResponse> redeem(
            @RequestHeader(value = "Idempotency-Key", required = true) final String idempotencyKey,
            @RequestBody @Valid final RedeemVoucherRequest body
    ) {
        final var merchantId = currentUserProvider.getCurrentMerchantId();
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();

        final var out = redeemVoucherUseCase.execute(new RedeemVoucherCommand(
                tenantId,
                merchantId,
                body.amountCents(),
                body.publicToken(),
                body.displayCode(),
                idempotencyKey
        ));

        return ResponseEntity.ok(RedeemVoucherResponse.of(out.voucherId(), out.newBalanceCents()));
    }

    @PostMapping("/reversal")
    // [merchant_admin]
    // remover tenant id do body e colocar via token
    @PreAuthorize("hasRole('merchant_admin')")
    public ResponseEntity<RedeemVoucherResponse> reversal(
            @RequestHeader(value = "Idempotency-Key", required = true) final String idempotencyKey,
            @RequestBody @Valid final ReverseVoucherRequest body
    ) {
        final var merchantId = currentUserProvider.getCurrentMerchantId();
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();

        final var out = reverseRedeemUseCase.execute(new ReverseRedeemCommand(
                tenantId,
                merchantId,
                body.refLedgerEntryId(),
                body.publicToken(),
                body.displayCode(),
                idempotencyKey
        ));

        return ResponseEntity.ok(RedeemVoucherResponse.of(out.voucherId(), out.newBalanceCents()));
    }

    @GetMapping("{publicToken}")
    public ResponseEntity<GetByTokenOutput> redeem(
            @PathVariable String publicToken
    ) {
        final var response = getByTokenUseCase.execute(new GetByTokenCommand(publicToken));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/display-code/{displayCode}")
    @PreAuthorize("hasAnyRole('merchant_admin', 'merchant_operator')")
    public ResponseEntity<GetByDisplayCodeOutput> getByDisplayCode(
            @PathVariable String displayCode
    ) {
        final var merchantId = currentUserProvider.getCurrentMerchantId();
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();
        final var response = getByDisplayCodeUseCase.execute(
                new GetByDisplayCodeCommand(tenantId, displayCode)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    // [tenant_admin, tenant_operator]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasAnyRole('tenant_admin', 'tenant_operator')")
    public ResponseEntity<Pagination<ListByTenantOutput>> listByTenant(
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "active", required = false) final Boolean active,
            @RequestParam(name = "campaignName", required = false) final String campaignName,
            @RequestParam(name = "displayCode", required = false) final String displayCode,
            @RequestParam(name = "buyerName", required = false) final String buyerName,
            @RequestParam(name = "buyerPhone", required = false) final String buyerPhone
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        final var buyerNameFilter =
                buyerName != null && !buyerName.isBlank() ? buyerName.trim() : null;
        final var buyerPhoneFilter =
                buyerPhone != null && !buyerPhone.isBlank() ? buyerPhone.trim() : null;

        final var searchQuery = new SearchVoucherQuery(
                page,
                perPage,
                active != null && active,
                campaignName,
                displayCode,
                buyerNameFilter,
                buyerPhoneFilter
        );
        final var command = new ListByTenantCommand(tenantId, searchQuery);
        return ResponseEntity.ok(listByTenantUseCase.execute(command));
    }

    @GetMapping("/ledger-entries")
    // [merchant_admin, merchant_operator]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasAnyRole('merchant_admin', 'merchant_operator')")
    public ResponseEntity<Pagination<ListLedgerEntriesByMerchantOutput>> getLedgerEntries(
            @RequestParam(name = "search", required = false, defaultValue = "") final String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "createdAt") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "desc") final String direction
    ) {
        final var merchantId = currentUserProvider.getCurrentMerchantId();
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();

        final var searchQuery = new SearchQuery(
                page,
                perPage,
                search,
                sort,
                direction
        );

        final var command = new ListLedgerEntriesByMerchantCommand(
                tenantId,
                merchantId,
                searchQuery
        );

        final var response = listLedgerEntriesByMerchantUseCase.execute(command);
        return ResponseEntity.ok(response);
    }
}