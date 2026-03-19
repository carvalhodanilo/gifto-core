package com.vp.core.application.merchant.update.impl;

import com.vp.core.application.merchant.update.UpdateMerchantCommand;
import com.vp.core.application.merchant.update.UpdateMerchantOutput;
import com.vp.core.application.merchant.update.UpdateMerchantUseCase;
import com.vp.core.domain.exceptions.NotFoundException;
import com.vp.core.domain.gateway.MerchantGateway;
import com.vp.core.domain.merchant.Merchant;
import com.vp.core.domain.merchant.MerchantId;
import com.vp.core.domain.tenant.TenantId;
import com.vp.core.domain.valueObjects.Email;
import com.vp.core.domain.valueObjects.Location;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateMerchantUseCaseImpl extends UpdateMerchantUseCase {

    private final MerchantGateway merchantGateway;

    public UpdateMerchantUseCaseImpl(final MerchantGateway merchantGateway) {
        this.merchantGateway = merchantGateway;
    }

    @Override
    @Transactional
    public UpdateMerchantOutput execute(final UpdateMerchantCommand command) {
        final var merchantId = MerchantId.from(command.merchantId());
        final var tenantId = TenantId.from(command.tenantId());

        final var merchant = merchantGateway.findByIdAndTenantId(merchantId, tenantId)
                .orElseThrow(() -> NotFoundException.with(Merchant.class, merchantId));

        merchant.updateProfile(
                command.name(),
                command.fantasyName(),
                command.phone1(),
                command.phone2(),
                Email.with(command.email()),
                command.url()
        );

        if (hasLocation(command)) {
            final var location = buildLocation(
                    command.street(), command.number(), command.neighborhood(), command.complement(),
                    command.city(), command.state(), command.country(), command.postalCode()
            );
            merchant.updateLocation(location);
        }

        merchantGateway.update(merchant);
        return UpdateMerchantOutput.of(merchantId.getValue());
    }

    private static boolean hasLocation(final UpdateMerchantCommand command) {
        return command.street() != null || command.number() != null || command.neighborhood() != null
                || command.complement() != null || command.city() != null || command.state() != null
                || command.country() != null || command.postalCode() != null;
    }

    private static Location buildLocation(
            final String street,
            final String number,
            final String neighborhood,
            final String complement,
            final String city,
            final String state,
            final String country,
            final String postalCode
    ) {
        if (street == null && number == null && neighborhood == null && complement == null
                && city == null && state == null && country == null && postalCode == null) {
            return Location.empty();
        }
        return Location.with(
                blankToNull(street),
                blankToNull(number),
                blankToNull(neighborhood),
                blankToNull(complement),
                blankToNull(city),
                blankToNull(state),
                blankToNull(country),
                blankToNull(postalCode)
        );
    }

    private static String blankToNull(final String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
