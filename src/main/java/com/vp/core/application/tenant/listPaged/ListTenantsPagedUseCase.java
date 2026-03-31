package com.vp.core.application.tenant.listPaged;

import com.vp.core.application.UseCase;
import com.vp.core.domain.pagination.Pagination;

public abstract class ListTenantsPagedUseCase extends UseCase<ListTenantsPagedCommand, Pagination<ListTenantsPagedOutput>> {
}

