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
import com.vp.core.application.campaign.uploadBanner.UploadCampaignBannerCommand;
import com.vp.core.application.campaign.uploadBanner.UploadCampaignBannerUseCase;
import com.vp.core.application.security.AccessScopeService;
import com.vp.core.application.security.CurrentUserProvider;
import com.vp.core.infrastructure.api.request.CreateCampaignRequest;
import com.vp.core.infrastructure.api.request.UpdateCampaignRequest;
import com.vp.core.infrastructure.api.response.AssetUrlResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;

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
    private final UploadCampaignBannerUseCase uploadCampaignBannerUseCase;
    private final CurrentUserProvider currentUserProvider;
    private final AccessScopeService accessScopeService;

    public CampaignController(
            final FindAllActiveByTenantUseCase findAllActiveByTenantUseCase,
            final CreateCampaignUseCase createCampaignUseCase,
            final GetAllByTenantUseCase getAllByTenantUseCase,
            final ActivateCampaignUseCase activateCampaignUseCase,
            final PauseCampaignUseCase pauseCampaignUseCase,
            final SuspendCampaignUseCase suspendCampaignUseCase,
            final UpdateCampaignUseCase updateCampaignUseCase,
            final UploadCampaignBannerUseCase uploadCampaignBannerUseCase,
            final CurrentUserProvider currentUserProvider,
            final AccessScopeService accessScopeService
    ) {
        this.findAllActiveByTenantUseCase = findAllActiveByTenantUseCase;
        this.createCampaignUseCase = createCampaignUseCase;
        this.getAllByTenantUseCase = getAllByTenantUseCase;
        this.activateCampaignUseCase = activateCampaignUseCase;
        this.pauseCampaignUseCase = pauseCampaignUseCase;
        this.suspendCampaignUseCase = suspendCampaignUseCase;
        this.updateCampaignUseCase = updateCampaignUseCase;
        this.uploadCampaignBannerUseCase = uploadCampaignBannerUseCase;
        this.currentUserProvider = currentUserProvider;
        this.accessScopeService = accessScopeService;
    }

    @GetMapping
    //[tenant_admin, tenant_operator]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasAnyRole('tenant_admin', 'tenant_operator')")
    public ResponseEntity<FindAllActiveByTenantOutput> findAllActiveByMerchant(
            // tenantId resolvido via token
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        final var command = new FindAllActiveByTenantCommand(tenantId);
        return ResponseEntity.ok(findAllActiveByTenantUseCase.execute(command));
    }

    @PostMapping
    //[tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<CreateCampaignOutput> create(
            @RequestBody CreateCampaignRequest request
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

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
    //[tenant_admin, tenant_operator]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasAnyRole('tenant_admin', 'tenant_operator')")
    public ResponseEntity<GetAllByTenantOutput> findAllByTenant(
            // tenantId resolvido via token
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        final var command = new GetAllByTenantCommand(tenantId);
        return ResponseEntity.ok(getAllByTenantUseCase.execute(command));
    }

    @PutMapping("/{campaignId}/update")
    //[tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<Void> update(
            @PathVariable String campaignId,
            @RequestBody UpdateCampaignRequest request
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

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

    @PostMapping(value = "/{campaignId}/banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //[tenant_admin]
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<AssetUrlResponse> uploadBanner(
            @PathVariable String campaignId,
            @RequestPart("file") MultipartFile file
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo é obrigatório.");
        }
        final byte[] content;
        try {
            content = file.getBytes();
        } catch (final IOException e) {
            throw new IllegalArgumentException("Não foi possível ler o arquivo enviado.");
        }
        final var contentType = file.getContentType() != null ? file.getContentType() : "";
        final var out = uploadCampaignBannerUseCase.execute(
                new UploadCampaignBannerCommand(tenantId, campaignId, content, contentType));
        return ResponseEntity.ok(AssetUrlResponse.of(out.url()));
    }

    @PatchMapping("/{campaignId}/activate")
    //[tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<Void> activate(
            @PathVariable String campaignId
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        final var command = new ActivateCampaignCommand(tenantId, campaignId);
        activateCampaignUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{campaignId}/pause")
    //[tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<Void> pause(
            @PathVariable String campaignId
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        final var command = new PauseCampaignCommand(tenantId, campaignId);
        pauseCampaignUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{campaignId}/suspend")
    //[tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<Void> suspend(
            @PathVariable String campaignId
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        final var command = new SuspendCampaignCommand(tenantId, campaignId);
        suspendCampaignUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
}