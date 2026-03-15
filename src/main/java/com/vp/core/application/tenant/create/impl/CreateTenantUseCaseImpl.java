package com.vp.core.application.tenant.create.impl;

import com.vp.core.application.tenant.create.CreateTenantCommand;
import com.vp.core.application.tenant.create.CreateTenantOutput;
import com.vp.core.application.tenant.create.CreateTenantUseCase;
import com.vp.core.domain.campaign.Campaign;
import com.vp.core.domain.gateway.CampaignGateway;
import com.vp.core.domain.gateway.NetworkGateway;
import com.vp.core.domain.gateway.TenantGateway;
import com.vp.core.domain.gateway.UserGateway;
import com.vp.core.domain.network.Network;
import com.vp.core.domain.tenant.Tenant;
import com.vp.core.domain.user.User;
import com.vp.core.domain.valueObjects.Document;
import com.vp.core.domain.valueObjects.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTenantUseCaseImpl extends CreateTenantUseCase {

    private final TenantGateway tenantGateway;
    private final NetworkGateway networkGateway;
    private final CampaignGateway campaignGateway;
    private final UserGateway userGateway;

    public CreateTenantUseCaseImpl(
            final TenantGateway tenantGateway,
            final NetworkGateway networkGateway,
            final CampaignGateway campaignGateway,
            final UserGateway userGateway
    ) {
        this.tenantGateway = tenantGateway;
        this.networkGateway = networkGateway;
        this.campaignGateway = campaignGateway;
        this.userGateway = userGateway;
    }

    @Override
    @Transactional
    public CreateTenantOutput execute(final CreateTenantCommand command) {
        final var tenant = Tenant.create(
                command.legalName(),
                command.fantasyName(),
                Document.with(command.document()),
                command.phone1(),
                Email.with(command.contactEmail()),
                command.url()
        );
        tenantGateway.create(tenant);
        final var tenantId = tenant.getId();

        final var defaultNetwork = Network.createDefault(tenantId);
        networkGateway.create(defaultNetwork);

        final var defaultCampaign = Campaign.createDefault(tenantId, defaultNetwork.getId());
        campaignGateway.create(defaultCampaign);

        final var adminUser = User.inviteTenantUser(
                Email.with(command.contactEmail()),
                command.contactName(),
                tenantId
        );
        userGateway.create(adminUser);

        return CreateTenantOutput.of(
                tenantId.getValue(),
                defaultNetwork.getId().getValue(),
                defaultCampaign.getId().getValue(),
                adminUser.getId().getValue()
        );
    }
}
