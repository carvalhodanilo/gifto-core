package com.vp.core.application.storage;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Regras alinhadas ao frontend (imageUploadPresets): tipo, tamanho em bytes e dimensões (raster).
 */
@Component
public class ImageAssetValidator {

    private static final Set<String> TENANT_MIMES = Set.of(
            "image/png", "image/webp", "image/svg+xml"
    );
    private static final Set<String> BANNER_MIMES = Set.of(
            "image/png", "image/jpeg", "image/jpg", "image/webp"
    );
    private static final Set<String> MERCHANT_MIMES = Set.of(
            "image/png", "image/webp"
    );

    private static final long TENANT_MAX = 2L * 1024 * 1024;
    private static final long BANNER_MAX = 5L * 1024 * 1024;
    private static final long MERCHANT_MAX = 1024 * 1024;

    public void validate(
            final ImageAssetKind kind,
            final String contentType,
            final long sizeBytes,
            final byte[] content
    ) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content-Type da imagem é obrigatório.");
        }
        final var mime = contentType.toLowerCase(Locale.ROOT).trim();
        switch (kind) {
            case TENANT_LOGO -> validateTenant(mime, sizeBytes, content);
            case CAMPAIGN_BANNER -> validateBanner(mime, sizeBytes, content);
            case MERCHANT_LANDING_LOGO -> validateMerchant(mime, sizeBytes, content);
        }
    }

    private static void validateTenant(final String mime, final long sizeBytes, final byte[] content) {
        if (!TENANT_MIMES.contains(mime)) {
            throw new IllegalArgumentException("Tipo não permitido para logo do tenant. Use PNG, WebP ou SVG.");
        }
        if (sizeBytes > TENANT_MAX) {
            throw new IllegalArgumentException("Arquivo muito grande para logo do tenant (máx. 2 MB).");
        }
        if ("image/svg+xml".equals(mime)) {
            return;
        }
        final var dims = readDimensions(content);
        if (dims.w < 128 || dims.w > 2048 || dims.h < 128 || dims.h > 2048) {
            throw new IllegalArgumentException("Logo (raster): cada lado deve estar entre 128 e 2048 px.");
        }
        if (Math.max(dims.w, dims.h) < 256) {
            throw new IllegalArgumentException("Logo (raster): a maior dimensão deve ter pelo menos 256 px.");
        }
    }

    private static void validateBanner(final String mime, final long sizeBytes, final byte[] content) {
        if (!BANNER_MIMES.contains(mime)) {
            throw new IllegalArgumentException("Tipo não permitido para banner. Use JPEG, PNG ou WebP.");
        }
        if (sizeBytes > BANNER_MAX) {
            throw new IllegalArgumentException("Arquivo muito grande para banner (máx. 5 MB).");
        }
        final var dims = readDimensions(content);
        if (dims.w < 1200 || dims.w > 4096) {
            throw new IllegalArgumentException("Banner: largura entre 1200 e 4096 px.");
        }
        if (dims.h < 300 || dims.h > 2048) {
            throw new IllegalArgumentException("Banner: altura entre 300 e 2048 px.");
        }
        final double ratio = (double) dims.w / dims.h;
        if (ratio < 2.0 || ratio > 4.0) {
            throw new IllegalArgumentException("Banner: proporção largura/altura entre 2:1 e 4:1.");
        }
    }

    private static void validateMerchant(final String mime, final long sizeBytes, final byte[] content) {
        if (!MERCHANT_MIMES.contains(mime)) {
            throw new IllegalArgumentException("Tipo não permitido para logo da loja. Use PNG ou WebP.");
        }
        if (sizeBytes > MERCHANT_MAX) {
            throw new IllegalArgumentException("Arquivo muito grande para logo da loja (máx. 1 MB).");
        }
        final var dims = readDimensions(content);
        if (dims.w < 96 || dims.w > 512 || dims.h < 96 || dims.h > 512) {
            throw new IllegalArgumentException("Logo da loja: largura e altura entre 96 e 512 px.");
        }
        final double ratio = (double) dims.w / dims.h;
        if (Math.abs(ratio - 1.0) > 0.35) {
            throw new IllegalArgumentException("Logo da loja: use imagem quase quadrada (proporção próxima de 1:1).");
        }
    }

    private record Dims(int w, int h) {}

    private static Dims readDimensions(final byte[] content) {
        try {
            final BufferedImage img = ImageIO.read(new ByteArrayInputStream(content));
            if (img == null) {
                throw new IllegalArgumentException("Não foi possível ler dimensões da imagem.");
            }
            return new Dims(img.getWidth(), img.getHeight());
        } catch (final IOException e) {
            throw new IllegalArgumentException("Imagem inválida ou corrompida.");
        }
    }

    public static String extensionForMime(final String contentType) {
        final var mime = contentType.toLowerCase(Locale.ROOT).trim();
        return switch (mime) {
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            case "image/svg+xml" -> "svg";
            case "image/jpeg", "image/jpg" -> "jpg";
            default -> "bin";
        };
    }
}
