package com.vp.core.domain.gateway;

import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.campaign.CampaignId;
import com.vp.core.domain.tenant.TenantId;

import java.util.List;
import java.util.Optional;

public interface CampaignGateway extends Gateway<Campaign, CampaignId> {

    Optional<Campaign> findByTenantIdAndId(TenantId tenantId, CampaignId campaignId);

    List<Campaign> findAllActiveByTenantId(TenantId tenantId);

    List<Campaign> findAllByTenantId(TenantId tenantId);
}
