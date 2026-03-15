package com.vp.core.application.settlement.runBatch;

import com.vp.core.domain.tenant.TenantId;

/**
 * Comando para executar o batch de settlement.
 * O período é sempre a semana ISO anterior (fechada), gerado no use case; o período atual permanece em aberto.
 */
public record RunSettlementBatchCommand(
        TenantId tenantId
) {
}
