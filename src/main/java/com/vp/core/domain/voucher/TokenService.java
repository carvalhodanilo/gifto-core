package com.vp.core.domain.voucher;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

public final class TokenService {

    private static final char[] ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private static final int BASE = 32;

    private static final SecureRandom RNG = new SecureRandom();

    // Ajuste aqui:
    // - BODY_LEN: parte "aleatória"
    // - CHECKSUM_LEN: 1 char recomendado para evitar erro de digitação
    private final int bodyLength;
    private final boolean useChecksum;

    private final String voucherBaseUrl; // ex: "https://www.meudomain.com.br/voucher/"
    private final int tokenVersion;

    private final byte[] pepperV1; // segredo do servidor (NUNCA no banco/log)

    public TokenService(
            final String voucherBaseUrl,
            final String pepperV1,
            final int bodyLength,
            final boolean useChecksum
    ) {
        this.voucherBaseUrl = ensureEndsWithSlash(voucherBaseUrl);
        this.pepperV1 = pepperV1.getBytes(StandardCharsets.UTF_8);
        this.bodyLength = bodyLength;
        this.useChecksum = useChecksum;
        this.tokenVersion = 1;
    }

    public int tokenVersion() {
        return tokenVersion;
    }

    public String generateToken() {
        final char[] body = new char[bodyLength];
        for (int i = 0; i < bodyLength; i++) {
            body[i] = ALPHABET[RNG.nextInt(BASE)];
        }

        if (!useChecksum) {
            return new String(body);
        }

        final char checksum = checksumChar(body);
        final char[] full = Arrays.copyOf(body, bodyLength + 1);
        full[bodyLength] = checksum;
        return new String(full);
    }

    /**
     * Exibe publicToken com hífen para leitura (opcional).
     * Ex: "HFE4-4592-0K"
     */
    public String formatForDisplay(final String token) {
        final String t = normalize(token);
        // você pode ajustar o pattern; aqui: 4-4-resto
        if (t.length() <= 4) return t;
        if (t.length() <= 8) return t.substring(0, 4) + "-" + t.substring(4);
        return t.substring(0, 4) + "-" + t.substring(4, 8) + "-" + t.substring(8);
    }

    /**
     * Conteúdo do QR: "https://www.meudomain.com.br/voucher/<TOKEN>"
     */
    public String buildVoucherUrl(final String token) {
        return voucherBaseUrl + normalize(token);
    }

    /**
     * Calcula o tokenHash a partir do publicToken digitável.
     * Guarda isso no banco (hex string).
     *
     * Estratégia:
     *  - HMAC-SHA256(token_normalized, pepper_v1)
     */
    public String tokenHashHex(final String token) {
        final String normalized = normalize(token);
        final byte[] mac = hmacSha256(pepperV1, normalized.getBytes(StandardCharsets.UTF_8));
        return toHex(mac);
    }

    /**
     * Valida checksum (se habilitado).
     */
    public boolean isChecksumValid(final String token) {
        if (!useChecksum) return true;

        final String t = normalize(token);
        if (t.length() < 2) return false;

        final char[] body = t.substring(0, t.length() - 1).toCharArray();
        final char expected = checksumChar(body);
        final char actual = t.charAt(t.length() - 1);
        return expected == actual;
    }

    /**
     * Normaliza para comparação/busca:
     * - remove "-" e espaços
     * - upper-case
     */
    public String normalize(final String token) {
        if (token == null) return null;
        final StringBuilder sb = new StringBuilder(token.length());
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c == '-' || c == ' ' || c == '\t' || c == '\n' || c == '\r') continue;
            sb.append(Character.toUpperCase(c));
        }
        return sb.toString();
    }

    // -----------------------
    // Checksum (simples e eficaz)
    // -----------------------
    // A ideia aqui é detectar erro de digitação comum.
    // Implementação: soma ponderada dos valores base32 mod 32.
    // (Não é criptográfico, só anti-typo)
    private static char checksumChar(final char[] body) {
        int acc = 0;
        for (int i = 0; i < body.length; i++) {
            final int v = valueOf(body[i]);
            // peso simples (i+1) pra reduzir colisões triviais
            acc += (v * (i + 1));
        }
        final int mod = acc % BASE;
        return ALPHABET[mod];
    }

    private static int valueOf(final char c) {
        // aceita apenas nosso alfabeto (rápido)
        // se você quiser tolerar input tipo 'o'->'0', 'i'->'1', dá pra mapear aqui.
        final char up = Character.toUpperCase(c);
        for (int i = 0; i < ALPHABET.length; i++) {
            if (ALPHABET[i] == up) return i;
        }
        throw new IllegalArgumentException("Invalid publicToken character: " + c);
    }

    // -----------------------
    // HMAC + util
    // -----------------------

    private static byte[] hmacSha256(final byte[] key, final byte[] message) {
        try {
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(message);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC-SHA256 failed", e);
        }
    }

    private static String toHex(final byte[] bytes) {
        final char[] hex = new char[bytes.length * 2];
        final char[] digits = "0123456789abcdef".toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hex[i * 2] = digits[v >>> 4];
            hex[i * 2 + 1] = digits[v & 0x0F];
        }
        return new String(hex);
    }

    private static String ensureEndsWithSlash(final String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("voucherBaseUrl is required");
        }
        return baseUrl.endsWith("/") ? baseUrl : (baseUrl + "/");
    }
}
