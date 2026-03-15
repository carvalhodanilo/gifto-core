package com.vp.core.application.voucher.listByTenant;

import com.vp.core.application.UseCase;
import com.vp.core.domain.pagination.Pagination;

public abstract class ListByTenantUseCase extends UseCase<ListByTenantCommand, Pagination<ListByTenantOutput>> {
}
