package com.vp.core.domain.gateway;

import com.vp.core.domain.network.Network;
import com.vp.core.domain.network.NetworkId;
import com.vp.core.domain.tenant.TenantId;

import java.util.Optional;

public interface NetworkGateway extends Gateway<Network, NetworkId> {

    Optional<Network> findDefaultByTenantId(TenantId tenantId);
}
