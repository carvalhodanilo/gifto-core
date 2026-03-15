package com.vp.core.domain.gateway;

import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;

import java.util.List;

public interface TenantGateway extends Gateway<Tenant, TenantId> {

    boolean existsByDocumentValue(String documentValue);

    boolean existsByEmail(String email);

    List<Tenant> findAllActive();
}