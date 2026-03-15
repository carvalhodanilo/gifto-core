package com.vp.core.infrastructure.utils;

import org.springframework.data.jpa.domain.Specification;

public final class SpecificationUtils {

    private SpecificationUtils() {
    }

    public static <T> Specification<T> like(final String prop, final String term) {
        return (root, query, cb) ->  cb.like(cb.upper(root.get(prop)), SqlUtils.like(term.toUpperCase()));
    }

    public static <T> Specification<T> equal(final String prop, final String value) {
        return (root, query, cb) ->  cb.equal(cb.lower(root.get(prop)), value.toLowerCase());
    }

    public static <T> Specification<T> equal(final String prop, final Object value) {
        return (root, query, cb) -> cb.equal(root.get(prop), value);
    }
}
