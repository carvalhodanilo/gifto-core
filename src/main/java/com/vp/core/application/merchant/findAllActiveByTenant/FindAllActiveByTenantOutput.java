package com.vp.core.application.merchant.findAllActiveByTenant;

import com.vp.core.domain.merchant.Merchant;

import java.util.List;

public record FindAllActiveByTenantOutput(
        List<MerchantOutput> merchantList
) {
    public static FindAllActiveByTenantOutput of(
            List<Merchant> merchants
    ) {
        final var tenantOutputs = merchants.stream()
                .map(MerchantOutput::of)
                .toList();

        return new FindAllActiveByTenantOutput(tenantOutputs);
    }

    record MerchantOutput(
            String id,
            String merchantName
    ) {
        public static MerchantOutput of(final Merchant merchant) {
            return new MerchantOutput(
                    merchant.getId().getValue(),
                    merchant.name()
            );
        }
    }
}