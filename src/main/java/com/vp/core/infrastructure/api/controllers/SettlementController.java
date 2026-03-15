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

    /**
     * Executa o batch de settlement para o tenant (período = semana ISO anterior, já fechada).
     * O período atual não é usado pois ainda está em aberto. Se já existir batch para o período anterior, retorna erro.
     * tenantId obrigatório via header "tenant". Sem parâmetros no body.
     */
    @PostMapping("/batch/run")
    public ResponseEntity<RunSettlementBatchResponse> runBatch(
            @RequestHeader(name = "tenant", required = true) String tenantId
    ) {
        final var command = new RunSettlementBatchCommand(TenantId.from(tenantId));
        final var output = runSettlementBatchUseCase.execute(command);
        final var location = URI.create("/v1/settlements/batch/" + output.settlementBatchId());
        return ResponseEntity.created(location).body(RunSettlementBatchResponse.from(output));
    }

    /**
     * Busca o settlement do tenant para o período (ISO week: YYYY-Wnn, ex: 2026-W11).
     * tenantId obrigatório via header "tenant". PeriodKey inválido retorna 400; sem batch retorna 404.
     */
    @GetMapping("/batch/{periodKey}")
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

    /**
     * Marca uma entry de settlement como paga (informa referência de pagamento).
     * tenantId obrigatório via header "tenant". Batch ou entry inexistente retorna 404.
     */
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
