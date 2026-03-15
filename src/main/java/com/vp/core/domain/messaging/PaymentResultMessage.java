package com.vp.core.domain.messaging;

public record PaymentResultMessage(
        String orderId,
        String status,
        String paymentId,
        int attempt,
        String idempotencyKey,
        String reason,
        long processingMs
) { }
