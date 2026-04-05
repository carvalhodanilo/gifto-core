package com.vp.core.infrastructure.config.storage;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Só habilita o cliente S3 quando {@code app.storage.s3.bucket} tem texto.
 * Evita falha de arranque quando o YAML define {@code bucket: ${VAR:}} com VAR vazia.
 */
public final class S3BucketConfiguredCondition implements Condition {

    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        final String bucket = context.getEnvironment().getProperty("app.storage.s3.bucket");
        return StringUtils.hasText(bucket);
    }
}
