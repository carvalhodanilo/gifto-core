package com.vp.core.infrastructure.voucher.services;

import com.vp.core.application.VoucherTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class VoucherTokenServiceImpl implements VoucherTokenService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder B64_URL = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Encoder B64_STD = Base64.getEncoder().withoutPadding();

    private final byte[] secret;
    private final int tokenBytes;
    private final int tokenVersion;

    public VoucherTokenServiceImpl(
            @Value("${app.voucher.token.secret}") final String secret,
            @Value("${app.voucher.token.bytes:16}") final int tokenBytes,
            @Value("${app.voucher.token.version:1}") final int tokenVersion
    ) {
        this.secret = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        this.tokenBytes = tokenBytes;
        this.tokenVersion = tokenVersion;
    }

    @Override
    public TokenData generate() {
        final var raw = new byte[tokenBytes];
        RNG.nextBytes(raw);

        final var publicToken = B64_URL.encodeToString(raw);
        final var tokenHash = hmac(publicToken);

        return new TokenData(publicToken, tokenHash, tokenVersion);
    }

    @Override
    public String hash(final String publicToken) {
        return hmac(publicToken);
    }

    private String hmac(final String data) {
        try {
            final var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            final var digest = mac.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return B64_STD.encodeToString(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Token hash failure", e);
        }
    }
}