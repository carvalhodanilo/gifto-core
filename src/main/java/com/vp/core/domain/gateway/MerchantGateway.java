package com.vp.core.domain.gateway;

import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;

import java.util.List;
import java.util.Optional;

public interface MerchantGateway extends Gateway<Merchant, MerchantId> {

    Optional<Merchant> findByIdAndTenantId(MerchantId anId, TenantId tenantId);

    boolean existsByTenantIdAndDocumentValue(TenantId tenantId, String documentValue);

    boolean existsByTenantIdAndEmailValue(TenantId tenantId, String emailValue);

    List<Merchant> findAllActiveByTenantId(TenantId tenantId);

}