package com.vp.core.infrastructure.api.controllers;

import com.vp.core.application.tenant.create.CreateTenantCommand;
import com.vp.core.application.tenant.create.CreateTenantOutput;
import com.vp.core.application.tenant.create.CreateTenantUseCase;
import com.vp.core.application.tenant.get.GetTenantOutput;
import com.vp.core.application.tenant.get.GetTenantUseCase;
import com.vp.core.application.tenant.getAll.GetAllTenantsOutput;
import com.vp.core.application.tenant.getAll.GetAllTenantsUseCase;
import com.vp.core.application.tenant.listPaged.ListTenantsPagedCommand;
import com.vp.core.application.tenant.listPaged.ListTenantsPagedOutput;
import com.vp.core.application.tenant.listPaged.ListTenantsPagedUseCase;
import com.vp.core.application.tenant.update.UpdateTenantCommand;
import com.vp.core.application.tenant.update.UpdateTenantOutput;
import com.vp.core.application.tenant.update.UpdateTenantUseCase;
import com.vp.core.application.tenant.uploadLogo.UploadTenantLogoCommand;
import com.vp.core.application.tenant.uploadLogo.UploadTenantLogoUseCase;
import com.vp.core.application.tenant.getBankAccount.GetTenantBankAccountCommand;
import com.vp.core.application.tenant.getBankAccount.GetTenantBankAccountOutput;
import com.vp.core.application.tenant.getBankAccount.GetTenantBankAccountUseCase;
import com.vp.core.application.tenant.updateBankAccount.UpdateTenantBankAccountCommand;
import com.vp.core.application.tenant.updateBankAccount.UpdateTenantBankAccountOutput;
import com.vp.core.application.tenant.updateBankAccount.UpdateTenantBankAccountUseCase;
import com.vp.core.application.merchant.listByTenant.ListMerchantsByTenantOutput;
import com.vp.core.application.merchant.listByTenant.ListMerchantsByTenantUseCase;
import com.vp.core.application.merchant.listByTenant.ListMerchantsByTenantCommand;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchMerchantQuery;
import com.vp.core.domain.pagination.SearchTenantQuery;
import com.vp.core.domain.valueObjects.PixKey;
import com.vp.core.domain.valueObjects.URL;
import com.vp.core.infrastructure.api.request.CreateTenantRequest;
import com.vp.core.infrastructure.api.request.UpdateTenantBankAccountRequest;
import com.vp.core.infrastructure.api.request.UpdateTenantRequest;
import com.vp.core.infrastructure.api.response.AssetUrlResponse;
import com.vp.core.infrastructure.api.response.CreateTenantResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    private final CreateTenantUseCase createTenantUseCase;
    private final GetAllTenantsUseCase getAllTenantsUseCase;
    private final ListTenantsPagedUseCase listTenantsPagedUseCase;
    private final GetTenantUseCase getTenantUseCase;
    private final UpdateTenantUseCase updateTenantUseCase;
    private final ListMerchantsByTenantUseCase listMerchantsByTenantUseCase;
    private final UploadTenantLogoUseCase uploadTenantLogoUseCase;
    private final GetTenantBankAccountUseCase getTenantBankAccountUseCase;
    private final UpdateTenantBankAccountUseCase updateTenantBankAccountUseCase;

    public TenantController(
            final CreateTenantUseCase createTenantUseCase,
            final GetAllTenantsUseCase getAllTenantsUseCase,
            final ListTenantsPagedUseCase listTenantsPagedUseCase,
            final GetTenantUseCase getTenantUseCase,
            final UpdateTenantUseCase updateTenantUseCase,
            final ListMerchantsByTenantUseCase listMerchantsByTenantUseCase,
            final UploadTenantLogoUseCase uploadTenantLogoUseCase,
            final GetTenantBankAccountUseCase getTenantBankAccountUseCase,
            final UpdateTenantBankAccountUseCase updateTenantBankAccountUseCase
    ) {
        this.createTenantUseCase = createTenantUseCase;
        this.getAllTenantsUseCase = getAllTenantsUseCase;
        this.listTenantsPagedUseCase = listTenantsPagedUseCase;
        this.getTenantUseCase = getTenantUseCase;
        this.updateTenantUseCase = updateTenantUseCase;
        this.listMerchantsByTenantUseCase = listMerchantsByTenantUseCase;
        this.uploadTenantLogoUseCase = uploadTenantLogoUseCase;
        this.getTenantBankAccountUseCase = getTenantBankAccountUseCase;
        this.updateTenantBankAccountUseCase = updateTenantBankAccountUseCase;
    }

    @PostMapping
    // [system_admin]
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<CreateTenantResponse> create(@RequestBody final CreateTenantRequest body) {
        final CreateTenantOutput output = createTenantUseCase.execute(CreateTenantCommand.with(
                body.name(),
                body.fantasyName(),
                body.document(),
                body.phone1(),
                body.email(),
                body.url()
        ));

        final var location = URI.create("/tenants/" + output.tenantId());
        return ResponseEntity.created(location).body(CreateTenantResponse.from(output));
    }

    @GetMapping
    // [system_admin]
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<GetAllTenantsOutput> findAllActive() {
        return ResponseEntity.ok(getAllTenantsUseCase.execute());
    }

    @GetMapping("/paged")
    // [system_admin]
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<Pagination<ListTenantsPagedOutput>> listPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String document
    ) {
        final var query = new SearchTenantQuery(page, perPage, name, document);
        final var command = new ListTenantsPagedCommand(query);
        return ResponseEntity.ok(listTenantsPagedUseCase.execute(command));
    }

    @GetMapping("/{tenantId}")
    // [system_admin]
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<GetTenantOutput> getById(@PathVariable String tenantId) {
        return ResponseEntity.ok(getTenantUseCase.execute(tenantId));
    }

    @PostMapping(value = "/{tenantId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // [system_admin]
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<AssetUrlResponse> uploadLogo(
            @PathVariable String tenantId,
            @RequestPart("file") MultipartFile file
    ) {
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
        final var out = uploadTenantLogoUseCase.execute(new UploadTenantLogoCommand(tenantId, content, contentType));
        return ResponseEntity.ok(AssetUrlResponse.of(out.url()));
    }

    @PatchMapping("/{tenantId}")
    // [system_admin]
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<UpdateTenantOutput> update(
            @PathVariable String tenantId,
            @RequestBody @Valid UpdateTenantRequest body
    ) {
        final var out = updateTenantUseCase.execute(new UpdateTenantCommand(
                tenantId,
                body.name(),
                body.fantasyName(),
                body.phone1(),
                body.phone2(),
                body.email(),
                URL.with(body.url())
        ));
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{tenantId}/bank-account")
    // [system_admin]
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<GetTenantBankAccountOutput> getBankAccount(@PathVariable String tenantId) {
        final var command = new GetTenantBankAccountCommand(tenantId);
        return getTenantBankAccountUseCase.execute(command)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/{tenantId}/bank-account")
    // [system_admin]
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<UpdateTenantBankAccountOutput> updateBankAccount(
            @PathVariable String tenantId,
            @RequestBody final UpdateTenantBankAccountRequest body
    ) {
        final var pixKeyType = body.pixKeyType() != null && !body.pixKeyType().isBlank()
                ? PixKey.PixKeyType.valueOf(body.pixKeyType())
                : null;
        final var command = new UpdateTenantBankAccountCommand(
                tenantId,
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
        return ResponseEntity.ok(updateTenantBankAccountUseCase.execute(command));
    }

    @GetMapping("/{tenantId}/merchants")
    // [system_admin]
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<Pagination<ListMerchantsByTenantOutput>> listMerchantsByTenant(
            @PathVariable String tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(required = false) String terms,
            @RequestParam(required = false) String status
    ) {
        final var search = new SearchMerchantQuery(page, perPage, terms, status);
        final var command = new ListMerchantsByTenantCommand(tenantId, search);
        return ResponseEntity.ok(listMerchantsByTenantUseCase.execute(command));
    }
}