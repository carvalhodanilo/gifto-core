package com.vp.core.infrastructure.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateTenantRequest(
        @NotBlank String name,
        String fantasyName,
        String phone1,
        String phone2,
        @NotBlank @Email String email,
        @NotBlank String url
) {
}

