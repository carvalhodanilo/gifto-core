package com.vp.core.infrastructure.storage;

import com.vp.core.domain.storage.ObjectStorageGateway;
import com.vp.core.infrastructure.config.storage.S3StorageProperties;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class S3ObjectStorageGateway implements ObjectStorageGateway {

    private final S3Client s3Client;
    private final S3StorageProperties props;

    public S3ObjectStorageGateway(final S3Client s3Client, final S3StorageProperties props) {
        this.s3Client = s3Client;
        this.props = props;
    }

    @Override
    public String putPublicObject(
            final String key,
            final InputStream body,
            final long contentLength,
            final String contentType
    ) {
        final var req = PutObjectRequest.builder()
                .bucket(props.bucket())
                .key(key)
                .contentType(contentType)
                .build();
        s3Client.putObject(req, RequestBody.fromInputStream(body, contentLength));
        return publicUrlForKey(key);
    }

    private String publicUrlForKey(final String key) {
        if (StringUtils.hasText(props.publicBaseUrl())) {
            final var base = props.publicBaseUrl().replaceAll("/+$", "");
            return base + "/" + encodeKeyPath(key);
        }
        final var bucket = props.bucket();
        final var region = props.region();
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + encodeKeyPath(key);
    }

    private static String encodeKeyPath(final String key) {
        return Stream.of(key.split("/"))
                .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20"))
                .reduce((a, b) -> a + "/" + b)
                .orElse("");
    }
}
