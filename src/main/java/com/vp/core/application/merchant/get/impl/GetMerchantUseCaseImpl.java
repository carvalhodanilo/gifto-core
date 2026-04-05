package com.vp.core.application.merchant.get.impl;

import com.vp.core.application.merchant.get.GetMerchantCommand;
import com.vp.core.application.merchant.get.GetMerchantOutput;
import com.vp.core.application.merchant.get.GetMerchantUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.Location;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetMerchantUseCaseImpl extends GetMerchantUseCase {

    private final MerchantGateway merchantGateway;

    public GetMerchantUseCaseImpl(final MerchantGateway merchantGateway) {
        this.merchantGateway = merchantGateway;
    }

    @Override
    public GetMerchantOutput execute(final GetMerchantCommand command) {
        final var merchantId = MerchantId.from(command.merchantId());
        final var tenantId = TenantId.from(command.tenantId());

        final var merchant = merchantGateway.findByIdAndTenantId(merchantId, tenantId)
                .orElseThrow(() -> NotFoundException.with(Merchant.class, merchantId));

        final var location = toLocationOutput(merchant.getLocation());
        final var activeNetworkIds = merchant.networkLinks().stream()
                .filter(link -> link.isActive())
                .map(link -> link.networkId().getValue().toString())
                .collect(Collectors.toList());

        return GetMerchantOutput.of(
                merchant.getId().getValue(),
                merchant.getName(),
                merchant.getFantasyName(),
                merchant.getDocument() != null ? merchant.getDocument().getValue() : null,
                merchant.getEmail() != null ? merchant.getEmail().getValue() : null,
                merchant.getPhone1(),
                merchant.getPhone2(),
                merchant.getUrl() != null ? merchant.getUrl().getValue() : null,
                merchant.getLandingLogoUrl(),
                merchant.status().name(),
                location,
                activeNetworkIds,
                merchant.getCreatedAt(),
                merchant.getUpdatedAt()
        );
    }

    private static GetMerchantOutput.LocationOutput toLocationOutput(final Location loc) {
        if (loc == null) {
            return null;
        }
        return new GetMerchantOutput.LocationOutput(
                loc.getStreet(),
                loc.getNumber(),
                loc.getNeighborhood(),
                loc.getComplement(),
                loc.getCity(),
                loc.getState(),
                loc.getCountry(),
                loc.getPostalCode()
        );
    }
}
