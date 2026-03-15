package com.vp.core.infrastructure.api.controllers;

import com.vp.core.application.campaign.activate.ActivateCampaignCommand;
import com.vp.core.application.campaign.activate.ActivateCampaignUseCase;
import com.vp.core.application.campaign.create.CreateCampaignCommand;
import com.vp.core.application.campaign.create.CreateCampaignOutput;
import com.vp.core.application.campaign.create.CreateCampaignUseCase;
import com.vp.core.application.campaign.findAllActiveByTenant.FindAllActiveByTenantCommand;
import com.vp.core.application.campaign.findAllActiveByTenant.FindAllActiveByTenantOutput;
import com.vp.core.application.campaign.findAllActiveByTenant.FindAllActiveByTenantUseCase;
import com.vp.core.application.campaign.findAllByTenant.GetAllByTenantCommand;
import com.vp.core.application.campaign.findAllByTenant.GetAllByTenantOutput;
import com.vp.core.application.campaign.findAllByTenant.GetAllByTenantUseCase;
import com.vp.core.application.campaign.pause.PauseCampaignCommand;
import com.vp.core.application.campaign.pause.PauseCampaignUseCase;
import com.vp.core.application.campaign.suspend.SuspendCampaignCommand;
import com.vp.core.application.campaign.suspend.SuspendCampaignUseCase;
import com.vp.core.application.campaign.update.UpdateCampaignCommand;
import com.vp.core.application.campaign.update.UpdateCampaignUseCase;
import com.vp.core.infrastructure.api.request.CreateCampaignRequest;
import com.vp.core.infrastructure.api.request.UpdateCampaignRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    private final FindAllActiveByTenantUseCase findAllActiveByTenantUseCase;
    private final GetAllByTenantUseCase getAllByTenantUseCase;
    private final CreateCampaignUseCase createCampaignUseCase;
    private final ActivateCampaignUseCase activateCampaignUseCase;
    private final PauseCampaignUseCase pauseCampaignUseCase;
    private final SuspendCampaignUseCase suspendCampaignUseCase;
    private final UpdateCampaignUseCase updateCampaignUseCase;

    public CampaignController(
            final FindAllActiveByTenantUseCase findAllActiveByTenantUseCase,
            final CreateCampaignUseCase createCampaignUseCase,
            final GetAllByTenantUseCase getAllByTenantUseCase,
            final ActivateCampaignUseCase activateCampaignUseCase,
            final PauseCampaignUseCase pauseCampaignUseCase,
            final SuspendCampaignUseCase suspendCampaignUseCase,
            final UpdateCampaignUseCase updateCampaignUseCase
    ) {
        this.findAllActiveByTenantUseCase = findAllActiveByTenantUseCase;
        this.createCampaignUseCase = createCampaignUseCase;
        this.getAllByTenantUseCase = getAllByTenantUseCase;
        this.activateCampaignUseCase = activateCampaignUseCase;
        this.pauseCampaignUseCase = pauseCampaignUseCase;
        this.suspendCampaignUseCase = suspendCampaignUseCase;
        this.updateCampaignUseCase = updateCampaignUseCase;
    }

    @GetMapping
    public ResponseEntity<FindAllActiveByTenantOutput> findAllActiveByMerchant(
            @RequestParam(name = "tenantId", required = true) String tenantId
    ) {
        final var command = new FindAllActiveByTenantCommand(tenantId);
        return ResponseEntity.ok(findAllActiveByTenantUseCase.execute(command));
    }

    @PostMapping
    public ResponseEntity<CreateCampaignOutput> create(
            @RequestHeader(value = "tenant") final String tenantId,
            @RequestBody CreateCampaignRequest request
    ) {
        final var command = new CreateCampaignCommand(
                tenantId,
                request.name(),
                request.expirationDays(),
                request.startsAt(),
                request.endsAt()
        );
        return ResponseEntity.ok(createCampaignUseCase.execute(command));
    }

    @GetMapping("/all")
    public ResponseEntity<GetAllByTenantOutput> findAllByTenant(
            @RequestHeader(name = "tenant") String tenantId
    ) {
        final var command = new GetAllByTenantCommand(tenantId);
        return ResponseEntity.ok(getAllByTenantUseCase.execute(command));
    }

    @PutMapping("/{campaignId}/update")
    public ResponseEntity<Void> update(
            @RequestHeader(name = "tenant") String tenantId,
            @PathVariable String campaignId,
            @RequestBody UpdateCampaignRequest request
    ) {
        final var command = new UpdateCampaignCommand(
                tenantId,
                campaignId,
                request.name(),
                request.expirationDays(),
                request.startsAt(),
                request.endsAt()
        );

        updateCampaignUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{campaignId}/activate")
    public ResponseEntity<Void> activate(
            @RequestHeader(name = "tenant") String tenantId,
            @PathVariable String campaignId
    ) {
        final var command = new ActivateCampaignCommand(tenantId, campaignId);
        activateCampaignUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{campaignId}/pause")
    public ResponseEntity<Void> pause(
            @RequestHeader(name = "tenant") String tenantId,
            @PathVariable String campaignId
    ) {
        final var command = new PauseCampaignCommand(tenantId, campaignId);
        pauseCampaignUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{campaignId}/suspend")
    public ResponseEntity<Void> suspend(
            @RequestHeader(name = "tenant") String tenantId,
            @PathVariable String campaignId
    ) {
        final var command = new SuspendCampaignCommand(tenantId, campaignId);
        suspendCampaignUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
}