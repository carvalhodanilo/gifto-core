package com.vp.core.infrastructure.api.controllers;

import com.vp.core.application.settlement.getByTenantAndPeriod.GetSettlementByTenantAndPeriodCommand;
import com.vp.core.application.settlement.getByTenantAndPeriod.GetSettlementByTenantAndPeriodUseCase;
import com.vp.core.application.settlement.markAsPaid.MarkSettlementEntryAsPaidCommand;
import com.vp.core.application.settlement.markAsPaid.MarkSettlementEntryAsPaidUseCase;
import com.vp.core.application.settlement.runBatch.RunSettlementBatchCommand;
import com.vp.core.application.settlement.runBatch.RunSettlementBatchUseCase;
import com.vp.core.domain.settlement.SettlementBatchId;
import com.vp.core.domain.settlement.SettlementEntryId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.infrastructure.api.request.MarkSettlementEntryAsPaidRequest;
import com.vp.core.infrastructure.api.response.GetSettlementByTenantAndPeriodResponse;
import com.vp.core.infrastructure.api.response.MarkSettlementEntryAsPaidResponse;
import com.vp.core.infrastructure.api.response.RunSettlementBatchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/v1/settlements")
public class SettlementController {

    private final RunSettlementBatchUseCase runSettlementBatchUseCase;
    private final GetSettlementByTenantAndPeriodUseCase getSettlementByTenantAndPeriodUseCase;
    private final MarkSettlementEntryAsPaidUseCase markSettlementEntryAsPaidUseCase;

    public SettlementController(
            final RunSettlementBatchUseCase runSettlementBatchUseCase,
            final GetSettlementByTenantAndPeriodUseCase getSettlementByTenantAndPeriodUseCase,
            final MarkSettlementEntryAsPaidUseCase markSettlementEntryAsPaidUseCase
    ) {
        this.runSettlementBatchUseCase = runSettlementBatchUseCase;
        this.getSettlementByTenantAndPeriodUseCase = getSettlementByTenantAndPeriodUseCase;
        this.markSettlementEntryAsPaidUseCase = markSettlementEntryAsPaidUseCase;
    }

    @PostMapping("/batch/run")
    // [system_admin]
    // MANTER O HEADER
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<RunSettlementBatchResponse> runBatch(
            @RequestHeader(name = "tenant", required = true) String tenantId
    ) {
        final var command = new RunSettlementBatchCommand(TenantId.from(tenantId));
        final var output = runSettlementBatchUseCase.execute(command);
        final var location = URI.create("/v1/settlements/batch/" + output.settlementBatchId());
        return ResponseEntity.created(location).body(RunSettlementBatchResponse.from(output));
    }

    @GetMapping("/batch/{periodKey}")
    // [system_admin]
    // MANTER O HEADER
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<GetSettlementByTenantAndPeriodResponse> getByTenantAndPeriod(
            @RequestHeader(name = "tenant", required = true) String tenantId,
            @PathVariable String periodKey
    ) {
        final var command = new GetSettlementByTenantAndPeriodCommand(
                TenantId.from(tenantId),
                periodKey
        );
        final var output = getSettlementByTenantAndPeriodUseCase.execute(command);
        return ResponseEntity.ok(GetSettlementByTenantAndPeriodResponse.from(output));
    }

    // [system_admin]
    // MANTER O HEADER
    @PreAuthorize("hasRole('system_admin')")
    @PatchMapping("/batch/{batchId}/entries/{entryId}/paid")
    public ResponseEntity<MarkSettlementEntryAsPaidResponse> markEntryAsPaid(
            @RequestHeader(name = "tenant", required = true) String tenantId,
            @PathVariable String batchId,
            @PathVariable String entryId,
            @RequestBody MarkSettlementEntryAsPaidRequest request
    ) {
        final var command = new MarkSettlementEntryAsPaidCommand(
                SettlementBatchId.from(batchId),
                SettlementEntryId.from(entryId),
                request.paymentRef()
        );
        final var output = markSettlementEntryAsPaidUseCase.execute(command);
        return ResponseEntity.ok(MarkSettlementEntryAsPaidResponse.from(output));
    }
}
