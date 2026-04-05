package com.vp.core.infrastructure.config.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage.s3")
public record S3StorageProperties(
        String bucket,
        String region,
        String publicBaseUrl,
        String accessKeyId,
        String secretAccessKey
) {
    public S3StorageProperties {
        region = (region == null || region.isBlank()) ? "us-east-1" : region;
    }
}
