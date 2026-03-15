package com.vp.core.domain.gateway;

import com.vp.core.domain.settlement.SettlementBatch;
import com.vp.core.domain.settlement.SettlementBatchId;
import com.vp.core.domain.tenant.TenantId;

import java.util.Optional;

public interface SettlementBatchGateway extends Gateway<SettlementBatch, SettlementBatchId> {

    Optional<SettlementBatch> findByPeriodKey(String periodKey);

    Optional<SettlementBatch> findByTenantIdAndPeriodKey(TenantId tenantId, String periodKey);

}
