package com.vp.core.application.settlement.getByTenantAndPeriod;

import com.vp.core.domain.tenant.TenantId;

public record GetSettlementByTenantAndPeriodCommand(
        TenantId tenantId,
        String periodKey
) {
}
