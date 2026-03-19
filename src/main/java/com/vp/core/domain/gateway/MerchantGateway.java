package com.vp.core.domain.gateway;

import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchMerchantQuery;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.infrastructure.merchant.persistence.projection.MerchantListProjection;

import java.util.List;
import java.util.Optional;

public interface MerchantGateway extends Gateway<Merchant, MerchantId> {

    Optional<Merchant> findByIdAndTenantId(MerchantId anId, TenantId tenantId);

    boolean existsByTenantIdAndDocumentValue(TenantId tenantId, String documentValue);

    boolean existsByTenantIdAndEmailValue(TenantId tenantId, String emailValue);

    List<Merchant> findAllActiveByTenantId(TenantId tenantId);

    Pagination<MerchantListProjection> findAllByTenantId(TenantId tenantId, SearchMerchantQuery query);

}