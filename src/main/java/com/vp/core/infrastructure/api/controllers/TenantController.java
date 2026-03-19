package com.vp.core.infrastructure.api.controllers;

import com.vp.core.application.tenant.create.CreateTenantCommand;
import com.vp.core.application.tenant.create.CreateTenantOutput;
import com.vp.core.application.tenant.create.CreateTenantUseCase;
import com.vp.core.application.tenant.getAll.GetAllTenantsOutput;
import com.vp.core.application.tenant.getAll.GetAllTenantsUseCase;
import com.vp.core.infrastructure.api.request.CreateTenantRequest;
import com.vp.core.infrastructure.api.response.CreateTenantResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    private final CreateTenantUseCase createTenantUseCase;
    private final GetAllTenantsUseCase getAllTenantsUseCase;

    public TenantController(
            final CreateTenantUseCase createTenantUseCase,
            final GetAllTenantsUseCase getAllTenantsUseCase
    ) {
        this.createTenantUseCase = createTenantUseCase;
        this.getAllTenantsUseCase = getAllTenantsUseCase;
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
}