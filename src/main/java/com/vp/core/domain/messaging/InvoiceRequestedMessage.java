package com.vp.core.domain.messaging;

import java.math.BigDecimal;

public record InvoiceRequestedMessage(
        String eventId,
        String orderId,
        BigDecimal amount,
        String currency,
        String paidAt
) {}
