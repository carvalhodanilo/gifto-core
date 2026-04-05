package com.vp.core.domain.storage;

import java.io.InputStream;

/**
 * Port para armazenamento de objetos (ex.: S3). Implementação na infraestrutura.
 */
public interface ObjectStorageGateway {

    /**
     * Envia o objeto e retorna a URL pública de leitura (HTTP).
     */
    String putPublicObject(String key, InputStream body, long contentLength, String contentType);
}
