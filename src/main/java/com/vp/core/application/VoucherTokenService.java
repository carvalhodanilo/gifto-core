package com.vp.core.application;

public interface VoucherTokenService {

    TokenData generate();

    String hash(String publicToken);

    record TokenData(
            String publicToken,     // ex: "6zx4c6z5x4c6zx4c" (vai para o cliente/QR -> www.meudominio.com/voucher/6zx4c6z5x4c6zx4c)
            String tokenHash, // hash/HMAC para persistir no Voucher
            int tokenVersion  // rotação futura
    ) {}
}