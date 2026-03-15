package com.vp.core.infrastructure.voucher.services;

import com.vp.core.application.VoucherDisplayCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class VoucherDisplayCodeServiceImpl implements VoucherDisplayCodeService {

    private static final SecureRandom RNG = new SecureRandom();

    private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private final int length;

    public VoucherDisplayCodeServiceImpl(@Value("${app.voucher.display-code.length:12}") final int length) {
        this.length = length;
    }

    @Override
    public String generate() {
        final var buf = new char[length];
        for (int i = 0; i < length; i++) {
            buf[i] = ALPHABET[RNG.nextInt(ALPHABET.length)];
        }
        return new String(buf);
    }
}