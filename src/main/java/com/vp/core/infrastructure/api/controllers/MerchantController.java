package com.vp.core.infrastructure.api.controllers;

import com.vp.core.application.merchant.create.CreateMerchantCommand;
import com.vp.core.application.merchant.create.CreateMerchantUseCase;
import com.vp.core.application.merchant.findAllActiveByTenant.FindAllActiveByTenantCommand;
import com.vp.core.application.merchant.findAllActiveByTenant.FindAllActiveByTenantOutput;
import com.vp.core.application.merchant.findAllActiveByTenant.FindAllActiveByTenantUseCase;
import com.vp.core.infrastructure.api.request.CreateMerchantRequest;
import com.vp.core.infrastructure.api.response.CreateMerchantResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/merchants")
public class MerchantController {

    private final CreateMerchantUseCase createMerchantUseCase;
    private final FindAllActiveByTenantUseCase findAllActiveByTenantUseCase;

    public MerchantController(
            final CreateMerchantUseCase createMerchantUseCase,
            final FindAllActiveByTenantUseCase findAllActiveByTenantUseCase
    ) {
        this.createMerchantUseCase = createMerchantUseCase;
        this.findAllActiveByTenantUseCase = findAllActiveByTenantUseCase;
    }

    @PostMapping("/{tenantId}")
    public ResponseEntity<CreateMerchantResponse> create(
            @PathVariable final String tenantId,
            @RequestBody final CreateMerchantRequest body
    ) {
        final var output = createMerchantUseCase.execute(CreateMerchantCommand.with(
                tenantId,
                body.name(),
                body.fantasyName(),
                body.document(),
                body.phone1(),
                body.phone2(),
                body.email(),
                body.url()
        ));

        final var location = URI.create("/tenants/" + tenantId + "/merchants/" + output.merchantId());
        return ResponseEntity.created(location).body(CreateMerchantResponse.from(output));
    }

    @GetMapping("/active")
    public ResponseEntity<FindAllActiveByTenantOutput> findAllActiveByMerchant(
            @RequestHeader(name = "tenant", required = true) String tenantId
    ) {
        final var command = new FindAllActiveByTenantCommand(tenantId);
        return ResponseEntity.ok(findAllActiveByTenantUseCase.execute(command));
    }
}