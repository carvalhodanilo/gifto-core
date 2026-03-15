package com.vp.core.domain.gateway;

import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;
import com.vp.core.domain.pagination.SearchVoucherQuery;
import com.vp.core.domain.voucher.Voucher;
import com.vp.core.domain.voucher.VoucherId;
import com.vp.core.infrastructure.voucher.persistence.projection.VoucherIssuedProjection;

import java.util.List;
import java.util.Optional;

public interface VoucherGateway extends Gateway<Voucher, VoucherId> {

    Optional<Voucher> findByTokenHash(String tokenHash);
    Optional<Voucher> findByTokenHashAndDisplayCode(String tokenHash, String displayCode);
    Optional<Voucher> findByDisplayCode(String displayCode);
    Pagination<VoucherIssuedProjection> findAllByTenant(String tenantId, SearchVoucherQuery searchQuery);
}
