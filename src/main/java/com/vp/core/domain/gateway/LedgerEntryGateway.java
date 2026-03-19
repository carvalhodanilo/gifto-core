package com.vp.core.domain.gateway;

import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.pagination.SearchVoucherQuery;
import com.vp.core.domain.settlement.PeriodKey;
import com.vp.core.domain.settlement.SettlementBatchId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.voucher.LedgerEntry;
import com.vp.core.domain.voucher.LedgerEntryId;
import com.vp.core.infrastructure.voucher.persistence.projection.LedgerEntryListProjection;
import com.vp.core.infrastructure.voucher.persistence.projection.VoucherIssuedProjection;

import java.util.List;
import java.util.UUID;

public interface LedgerEntryGateway extends Gateway<LedgerEntry, LedgerEntryId> {

    List<LedgerEntry> findUnsettledRedeemAndReversal(TenantId tenantId, PeriodKey periodKey);

    void markAsSettled(List<LedgerEntryId> ids, SettlementBatchId batchId);

    Pagination<LedgerEntryListProjection> findAllByMerchant(UUID merchantId, SearchQuery searchQuery);
}
