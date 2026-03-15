package com.vp.core.infrastructure.api.controllers;

import com.vp.core.application.tenant.create.CreateTenantCommand;
import com.vp.core.application.tenant.create.CreateTenantOutput;
import com.vp.core.application.tenant.create.CreateTenantUseCase;
import com.vp.core.application.tenant.getAll.GetAllTenantsOutput;
import com.vp.core.application.tenant.getAll.GetAllTenantsUseCase;
import com.vp.core.application.voucher.listByTenant.ListByTenantCommand;
import com.vp.core.application.voucher.listByTenant.ListByTenantOutput;
import com.vp.core.application.voucher.listByTenant.ListByTenantUseCase;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchVoucherQuery;
import com.vp.core.infrastructure.api.request.CreateTenantRequest;
import com.vp.core.infrastructure.api.response.CreateTenantResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    private final CreateTenantUseCase createTenantUseCase;
    private final GetAllTenantsUseCase getAllTenantsUseCase;
    private final ListByTenantUseCase listByTenantUseCase;

    public TenantController(
            final CreateTenantUseCase createTenantUseCase,
            final GetAllTenantsUseCase getAllTenantsUseCase,
            final ListByTenantUseCase listByTenantUseCase
    ) {
        this.createTenantUseCase = createTenantUseCase;
        this.getAllTenantsUseCase = getAllTenantsUseCase;
        this.listByTenantUseCase = listByTenantUseCase;
    }

    @PostMapping
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
    public ResponseEntity<GetAllTenantsOutput> findAllActive() {
        return ResponseEntity.ok(getAllTenantsUseCase.execute());
    }

    @GetMapping("/{tenantId}/vouchers")
    public ResponseEntity<Pagination<ListByTenantOutput>> listByTenant(
            @PathVariable String tenantId,
            @RequestParam(name = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(name = "active", required = false) final Boolean active,
            @RequestParam(name = "campaignName", required = false) final String campaignName,
            @RequestParam(name = "displayCode", required = false) final String displayCode
    ) {
        final var searchQuery = new SearchVoucherQuery(
                page,
                perPage,
                active != null && active,
                campaignName,
                displayCode
        );

        final var command = new ListByTenantCommand(tenantId, searchQuery);
        return ResponseEntity.ok(listByTenantUseCase.execute(command));
    }
}