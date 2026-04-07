package com.vp.core.application.campaign.get;

import com.vp.core.application.UseCase;
import com.vp.core.application.campaign.findAllByTenant.GetAllByTenantOutput;

public abstract class GetCampaignUseCase
        extends UseCase<GetCampaignCommand, GetAllByTenantOutput.GetCampaignOutput> {
}
