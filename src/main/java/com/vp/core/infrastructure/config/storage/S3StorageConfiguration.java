package com.vp.core.infrastructure.config.storage;

import com.vp.core.domain.storage.ObjectStorageGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3StorageConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "app.storage.s3", name = "bucket")
    public S3Client s3Client(final S3StorageProperties props) {
        if (!StringUtils.hasText(props.bucket())) {
            throw new IllegalStateException("app.storage.s3.bucket must not be blank when set");
        }
        final var builder = S3Client.builder().region(Region.of(props.region()));
        if (StringUtils.hasText(props.accessKeyId()) && StringUtils.hasText(props.secretAccessKey())) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(props.accessKeyId(), props.secretAccessKey())));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnBean(S3Client.class)
    public ObjectStorageGateway s3ObjectStorageGateway(
            final S3Client s3Client,
            final S3StorageProperties props
    ) {
        return new com.vp.core.infrastructure.storage.S3ObjectStorageGateway(s3Client, props);
    }

    @Bean
    @ConditionalOnMissingBean(ObjectStorageGateway.class)
    public ObjectStorageGateway unconfiguredObjectStorageGateway() {
        return (key, body, contentLength, contentType) -> {
            throw new IllegalStateException(
                    "Armazenamento S3 não configurado: defina app.storage.s3.bucket (e credenciais se necessário).");
        };
    }
}
