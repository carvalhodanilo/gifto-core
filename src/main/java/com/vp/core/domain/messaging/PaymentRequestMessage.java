package com.vp.core.domain.messaging;

import java.math.BigDecimal;

public record PaymentRequestMessage(
        String orderId,
        BigDecimal amount,
        String currency,
        int attempt,
        String idempotencyKey
) { }

