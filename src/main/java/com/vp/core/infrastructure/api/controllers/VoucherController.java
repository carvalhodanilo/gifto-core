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
import com.vp.core.application.voucher.issue.IssueVoucherUseCase;
import com.vp.core.application.voucher.redeem.RedeemVoucherCommand;
import com.vp.core.application.voucher.redeem.RedeemVoucherUseCase;
import com.vp.core.application.voucher.reversal.ReverseRedeemCommand;
import com.vp.core.application.voucher.reversal.ReverseRedeemUseCase;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.infrastructure.api.request.IssueVoucherRequest;
import com.vp.core.infrastructure.api.request.RedeemVoucherRequest;
import com.vp.core.infrastructure.api.request.ReverseVoucherRequest;
import com.vp.core.infrastructure.api.response.IssueVoucherResponse;
import com.vp.core.infrastructure.api.response.RedeemVoucherResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public VoucherController(
            final IssueVoucherUseCase issueVoucherUseCase,
            final RedeemVoucherUseCase redeemVoucherUseCase,
            final GetByTokenUseCase getByTokenUseCase,
            final ReverseRedeemUseCase reverseRedeemUseCase,
            final GetByDisplayCodeUseCase getByDisplayCodeUseCase,
            final ListLedgerEntriesByMerchantUseCaseImpl listLedgerEntriesByMerchantUseCase
    ) {
        this.issueVoucherUseCase = issueVoucherUseCase;
        this.redeemVoucherUseCase = redeemVoucherUseCase;
        this.getByTokenUseCase = getByTokenUseCase;
        this.reverseRedeemUseCase = reverseRedeemUseCase;
        this.getByDisplayCodeUseCase = getByDisplayCodeUseCase;
        this.listLedgerEntriesByMerchantUseCase = listLedgerEntriesByMerchantUseCase;
    }

    @PostMapping("/issue")
    public ResponseEntity<IssueVoucherResponse> issue(
            @RequestHeader(value = "Idempotency-Key", required = false) final String idempotencyKeyHeader,
            @RequestBody @Valid final IssueVoucherRequest body
    ) {
        final var idempotencyKey =
                (idempotencyKeyHeader != null && !idempotencyKeyHeader.isBlank())
                        ? idempotencyKeyHeader
                        : (body.idempotencyKey() != null && !body.idempotencyKey().isBlank())
                        ? body.idempotencyKey()
                        : UUID.randomUUID().toString();

        final var out = issueVoucherUseCase.execute(new IssueVoucherCommand(
                body.tenantId(),
                body.campaignId(),
                body.amountCents(),
                idempotencyKey
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
    public ResponseEntity<RedeemVoucherResponse> redeem(
            @RequestHeader(value = "Idempotency-Key", required = true) final String idempotencyKey,
            @RequestBody @Valid final RedeemVoucherRequest body
    ) {
        final var out = redeemVoucherUseCase.execute(new RedeemVoucherCommand(
                body.tenantId(),
                body.merchantId(),
                body.amountCents(),
                body.publicToken(),
                body.displayCode(),
                idempotencyKey
        ));

        return ResponseEntity.ok(RedeemVoucherResponse.of(out.voucherId(), out.newBalanceCents()));
    }

    @PostMapping("/reversal")
    public ResponseEntity<RedeemVoucherResponse> reversal(
            @RequestHeader(value = "Idempotency-Key", required = true) final String idempotencyKey,
            @RequestBody @Valid final ReverseVoucherRequest body
    ) {
        final var out = reverseRedeemUseCase.execute(new ReverseRedeemCommand(
                body.tenantId(),
                body.merchantId(),
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
    public ResponseEntity<GetByDisplayCodeOutput> getByDisplayCode(
            @PathVariable String displayCode
    ) {
        final var response = getByDisplayCodeUseCase.execute(new GetByDisplayCodeCommand(displayCode));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ledger-entries")
    public ResponseEntity<Pagination<ListLedgerEntriesByMerchantOutput>> getLedgerEntries(
            @RequestHeader(value = "tenant", required = true) final String tenant,
            @RequestHeader(value = "merchant", required = true) final String merchant,
            @RequestParam(name = "search", required = false, defaultValue = "") final String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "createdAt") final String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "desc") final String direction
    ) {
        final var searchQuery = new SearchQuery(
                page,
                perPage,
                search,
                sort,
                direction
        );

        final var command = new ListLedgerEntriesByMerchantCommand(
                tenant,
                merchant,
                searchQuery
        );

        final var response = listLedgerEntriesByMerchantUseCase.execute(command);
        return ResponseEntity.ok(response);
    }
}