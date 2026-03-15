package com.vp.core.application.merchant.create.impl;

import com.vp.core.application.merchant.create.CreateMerchantCommand;
import com.vp.core.application.merchant.create.CreateMerchantOutput;
import com.vp.core.application.merchant.create.CreateMerchantUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.*;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.network.Network;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.user.User;
import com.vp.core.domain.valueObjects.BankAccount;
import com.vp.core.domain.valueObjects.Document;
import com.vp.core.domain.valueObjects.Email;
import com.vp.core.domain.valueObjects.Location;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMerchantUseCaseImpl extends CreateMerchantUseCase {

    private final MerchantGateway merchantGateway;
    private final NetworkGateway networkGateway;
    private final UserGateway userGateway;

    public CreateMerchantUseCaseImpl(
            MerchantGateway merchantGateway,
            final NetworkGateway networkGateway,
            final UserGateway userGateway
    ) {
        this.merchantGateway = merchantGateway;
        this.networkGateway = networkGateway;
        this.userGateway = userGateway;
    }

    @Override
    @Transactional
    public CreateMerchantOutput execute(final CreateMerchantCommand command) {
        final var tenantId = TenantId.from(command.tenantId());

        final var merchant = Merchant.create(
                tenantId,
                command.name(),
                command.fantasyName(),
                Document.with(command.document()),
                Location.empty(),
                BankAccount.empty(),
                command.phone1(),
                command.phone2(),
                Email.with(command.email()),
                command.url()
        );

        final var defaultNetwork = networkGateway.findDefaultByTenantId(tenantId)
                .orElseThrow(() -> NotFoundException.with(Network.class, tenantId));

        merchant.joinNetwork(defaultNetwork.getId());

        merchantGateway.create(merchant);
        final var merchantUser = User.inviteMerchantUser(
                Email.with(command.email()),
                command.name(),
                merchant.getId()
        );

        userGateway.create(merchantUser);
        return CreateMerchantOutput.of(merchant.getId().getValue());
    }
}
