package com.vp.core.infrastructure.api.controllers;

import com.vp.core.application.publiclanding.GetPublicCampaignLandingUseCase;
import com.vp.core.application.publiclanding.PublicTenantResolver;
import com.vp.core.infrastructure.api.response.PublicCampaignLandingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

/**
 * Dados agregados para a landing pública da campanha (QR do voucher). Sem JWT.
 */
@RestController
@RequestMapping("/public/campaign-landing")
public class PublicCampaignLandingController {

    private final PublicTenantResolver publicTenantResolver;
    private final GetPublicCampaignLandingUseCase getPublicCampaignLandingUseCase;

    public PublicCampaignLandingController(
            final PublicTenantResolver publicTenantResolver,
            final GetPublicCampaignLandingUseCase getPublicCampaignLandingUseCase
    ) {
        this.publicTenantResolver = publicTenantResolver;
        this.getPublicCampaignLandingUseCase = getPublicCampaignLandingUseCase;
    }

    /**
     * @param tenantId opcional — obrigatório quando o acesso é por IP ou sem subdomínio configurado (sandbox).
     * @param host     cabeçalho {@code Host} (injetado pelo cliente ou proxy).
     */
    @GetMapping("/{campaignId}")
    public ResponseEntity<PublicCampaignLandingResponse> get(
            @PathVariable final String campaignId,
            @RequestParam(name = "tenantId", required = false) final UUID tenantId,
            @RequestHeader(value = "Host", required = false) final String host
    ) {
        final UUID campaignUuid;
        try {
            campaignUuid = UUID.fromString(campaignId);
        } catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("campaignId deve ser um UUID válido.");
        }

        final UUID resolvedTenant = publicTenantResolver.resolve(Optional.ofNullable(tenantId), host);
        return ResponseEntity.ok(getPublicCampaignLandingUseCase.execute(resolvedTenant, campaignUuid));
    }
}
