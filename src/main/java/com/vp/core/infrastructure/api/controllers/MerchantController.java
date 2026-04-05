package com.vp.core.infrastructure.api.controllers;

import com.vp.core.application.merchant.create.CreateMerchantCommand;
import com.vp.core.application.merchant.create.CreateMerchantUseCase;
import com.vp.core.application.merchant.findAllActiveByTenant.FindAllActiveByTenantCommand;
import com.vp.core.application.merchant.findAllActiveByTenant.FindAllActiveByTenantOutput;
import com.vp.core.application.merchant.findAllActiveByTenant.FindAllActiveByTenantUseCase;
import com.vp.core.application.merchant.get.GetMerchantCommand;
import com.vp.core.application.merchant.get.GetMerchantOutput;
import com.vp.core.application.merchant.get.GetMerchantUseCase;
import com.vp.core.application.merchant.activate.ActivateMerchantCommand;
import com.vp.core.application.merchant.activate.ActivateMerchantUseCase;
import com.vp.core.application.merchant.getBankAccount.GetMerchantBankAccountCommand;
import com.vp.core.application.merchant.getBankAccount.GetMerchantBankAccountOutput;
import com.vp.core.application.merchant.getBankAccount.GetMerchantBankAccountUseCase;
import com.vp.core.application.merchant.suspend.SuspendMerchantCommand;
import com.vp.core.application.merchant.suspend.SuspendMerchantUseCase;
import com.vp.core.application.merchant.updateAccount.UpdateMerchantBankAccountCommand;
import com.vp.core.application.merchant.updateAccount.UpdateMerchantBankAccountOutput;
import com.vp.core.application.merchant.updateAccount.UpdateMerchantBankAccountUseCase;
import com.vp.core.application.merchant.listByTenant.ListMerchantsByTenantCommand;
import com.vp.core.application.merchant.listByTenant.ListMerchantsByTenantOutput;
import com.vp.core.application.merchant.listByTenant.ListMerchantsByTenantUseCase;
import com.vp.core.application.security.AccessScopeService;
import com.vp.core.application.security.CurrentUserProvider;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchMerchantQuery;
import com.vp.core.application.merchant.update.UpdateMerchantCommand;
import com.vp.core.application.merchant.update.UpdateMerchantOutput;
import com.vp.core.application.merchant.update.UpdateMerchantUseCase;
import com.vp.core.application.merchant.uploadLandingLogo.UploadMerchantLandingLogoCommand;
import com.vp.core.application.merchant.uploadLandingLogo.UploadMerchantLandingLogoUseCase;
import com.vp.core.domain.valueObjects.PixKey;
import com.vp.core.domain.valueObjects.URL;
import com.vp.core.infrastructure.api.request.CreateMerchantRequest;
import com.vp.core.infrastructure.api.request.UpdateMerchantBankAccountRequest;
import com.vp.core.infrastructure.api.request.UpdateMerchantRequest;
import com.vp.core.infrastructure.api.response.AssetUrlResponse;
import com.vp.core.infrastructure.api.response.CreateMerchantResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/merchants")
public class MerchantController {

    private final CreateMerchantUseCase createMerchantUseCase;
    private final FindAllActiveByTenantUseCase findAllActiveByTenantUseCase;
    private final ListMerchantsByTenantUseCase listMerchantsByTenantUseCase;
    private final GetMerchantUseCase getMerchantUseCase;
    private final GetMerchantBankAccountUseCase getMerchantBankAccountUseCase;
    private final UpdateMerchantUseCase updateMerchantUseCase;
    private final UpdateMerchantBankAccountUseCase updateMerchantBankAccountUseCase;
    private final ActivateMerchantUseCase activateMerchantUseCase;
    private final SuspendMerchantUseCase suspendMerchantUseCase;
    private final UploadMerchantLandingLogoUseCase uploadMerchantLandingLogoUseCase;
    private final CurrentUserProvider currentUserProvider;
    private final AccessScopeService accessScopeService;

    public MerchantController(
            final CreateMerchantUseCase createMerchantUseCase,
            final FindAllActiveByTenantUseCase findAllActiveByTenantUseCase,
            final ListMerchantsByTenantUseCase listMerchantsByTenantUseCase,
            final GetMerchantUseCase getMerchantUseCase,
            final GetMerchantBankAccountUseCase getMerchantBankAccountUseCase,
            final UpdateMerchantUseCase updateMerchantUseCase,
            final UpdateMerchantBankAccountUseCase updateMerchantBankAccountUseCase,
            final ActivateMerchantUseCase activateMerchantUseCase,
            final SuspendMerchantUseCase suspendMerchantUseCase,
            final UploadMerchantLandingLogoUseCase uploadMerchantLandingLogoUseCase,
            final CurrentUserProvider currentUserProvider,
            final AccessScopeService accessScopeService
    ) {
        this.createMerchantUseCase = createMerchantUseCase;
        this.findAllActiveByTenantUseCase = findAllActiveByTenantUseCase;
        this.listMerchantsByTenantUseCase = listMerchantsByTenantUseCase;
        this.getMerchantUseCase = getMerchantUseCase;
        this.getMerchantBankAccountUseCase = getMerchantBankAccountUseCase;
        this.updateMerchantUseCase = updateMerchantUseCase;
        this.updateMerchantBankAccountUseCase = updateMerchantBankAccountUseCase;
        this.activateMerchantUseCase = activateMerchantUseCase;
        this.suspendMerchantUseCase = suspendMerchantUseCase;
        this.uploadMerchantLandingLogoUseCase = uploadMerchantLandingLogoUseCase;
        this.currentUserProvider = currentUserProvider;
        this.accessScopeService = accessScopeService;
    }

    @PostMapping
    //[tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<CreateMerchantResponse> create(
            @RequestBody final CreateMerchantRequest body
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        final var loc = body.location();
        final var output = createMerchantUseCase.execute(CreateMerchantCommand.with(
                tenantId,
                body.name(),
                body.fantasyName(),
                body.document(),
                body.phone1(),
                body.phone2(),
                body.email(),
                body.url(),
                loc != null ? loc.street() : null,
                loc != null ? loc.number() : null,
                loc != null ? loc.neighborhood() : null,
                loc != null ? loc.complement() : null,
                loc != null ? loc.city() : null,
                loc != null ? loc.state() : null,
                loc != null ? loc.country() : null,
                loc != null ? loc.postalCode() : null
        ));

        final var location = URI.create("/tenants/" + tenantId + "/merchants/" + output.merchantId());
        return ResponseEntity.created(location).body(CreateMerchantResponse.from(output));
    }

    @GetMapping("/active")
    // [tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<FindAllActiveByTenantOutput> findAllActiveByMerchant(
            // tenantId resolvido via token
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        final var command = new FindAllActiveByTenantCommand(tenantId);
        return ResponseEntity.ok(findAllActiveByTenantUseCase.execute(command));
    }

    @GetMapping
    //[tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<Pagination<ListMerchantsByTenantOutput>> list(
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "terms", required = false) final String terms,
            @RequestParam(name = "status", required = false) final String status
    ) {
        final var tenantId = currentUserProvider.getCurrentTenantId();
        accessScopeService.ensureTenantAccess(tenantId);

        final var searchQuery = new SearchMerchantQuery(page, perPage, terms, status);
        final var command = new ListMerchantsByTenantCommand(tenantId, searchQuery);
        return ResponseEntity.ok(listMerchantsByTenantUseCase.execute(command));
    }

    @PutMapping("/{merchantId}")
    // [tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<UpdateMerchantOutput> update(
            @PathVariable String merchantId,
            @RequestBody final UpdateMerchantRequest body
    ) {
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();

        final var loc = body.location();
        final var command = new UpdateMerchantCommand(
                tenantId,
                merchantId,
                body.name(),
                body.fantasyName(),
                body.phone1(),
                body.phone2(),
                body.email(),
                URL.with(body.url()),
                loc != null ? loc.street() : null,
                loc != null ? loc.number() : null,
                loc != null ? loc.neighborhood() : null,
                loc != null ? loc.complement() : null,
                loc != null ? loc.city() : null,
                loc != null ? loc.state() : null,
                loc != null ? loc.country() : null,
                loc != null ? loc.postalCode() : null
        );
        return ResponseEntity.ok(updateMerchantUseCase.execute(command));
    }

    @PostMapping(value = "/{merchantId}/landing-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // [tenant_admin]
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<AssetUrlResponse> uploadLandingLogo(
            @PathVariable String merchantId,
            @RequestPart("file") MultipartFile file
    ) {
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();

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
        final var out = uploadMerchantLandingLogoUseCase.execute(
                new UploadMerchantLandingLogoCommand(tenantId, merchantId, content, contentType));
        return ResponseEntity.ok(AssetUrlResponse.of(out.url()));
    }

    @GetMapping("/{merchantId}")
    // remover header e utilizar via tenant/merchant id via token
    // [tenant_admin]
    @PreAuthorize("hasAnyRole('tenant_admin', 'merchant_admin', 'merchant_operator')")
    public ResponseEntity<GetMerchantOutput> get(
            @PathVariable String merchantId
    ) {
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();

        final var command = new GetMerchantCommand(tenantId, merchantId);
        return ResponseEntity.ok(getMerchantUseCase.execute(command));
    }

    @GetMapping("/{merchantId}/bank-account")
    // [tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<GetMerchantBankAccountOutput> getBankAccount(
            @PathVariable String merchantId
    ) {
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();

        final var command = new GetMerchantBankAccountCommand(tenantId, merchantId);
        return getMerchantBankAccountUseCase.execute(command)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/{merchantId}/bank-account")
    // [tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<UpdateMerchantBankAccountOutput> updateBankAccount(
            @PathVariable String merchantId,
            @RequestBody final UpdateMerchantBankAccountRequest body
    ) {
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();

        final var pixKeyType = body.pixKeyType() != null && !body.pixKeyType().isBlank()
                ? PixKey.PixKeyType.valueOf(body.pixKeyType())
                : null;
        final var command = new UpdateMerchantBankAccountCommand(
                tenantId,
                merchantId,
                body.bankCode(),
                body.bankName(),
                body.branch(),
                body.accountNumber(),
                body.accountDigit(),
                body.accountType(),
                body.holderName(),
                body.holderDocument(),
                pixKeyType,
                body.pixKeyValue()
        );
        return ResponseEntity.ok(updateMerchantBankAccountUseCase.execute(command));
    }

    @PostMapping("/{merchantId}/activate")
    // [tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<Void> activate(
            @PathVariable String merchantId
    ) {
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();

        activateMerchantUseCase.execute(new ActivateMerchantCommand(tenantId, merchantId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{merchantId}/suspend")
    // [tenant_admin]
    // remover header e utilizar via tenant/merchant id via token
    @PreAuthorize("hasRole('tenant_admin')")
    public ResponseEntity<Void> suspend(
            @PathVariable String merchantId
    ) {
        accessScopeService.ensureMerchantAccess(merchantId);
        final var tenantId = currentUserProvider.getCurrentTenantId();

        suspendMerchantUseCase.execute(new SuspendMerchantCommand(tenantId, merchantId));
        return ResponseEntity.noContent().build();
    }
}