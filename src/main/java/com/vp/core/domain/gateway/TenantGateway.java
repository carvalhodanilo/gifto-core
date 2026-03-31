package com.vp.core.domain.gateway;

import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchTenantQuery;
import com.vp.core.infrastructure.tenant.persistence.projection.TenantListProjection;

import java.util.List;

public interface TenantGateway extends Gateway<Tenant, TenantId> {

    boolean existsByDocumentValue(String documentValue);

    boolean existsByEmail(String email);

    List<Tenant> findAllActive();

    Pagination<TenantListProjection> findAllPaged(SearchTenantQuery query);
}