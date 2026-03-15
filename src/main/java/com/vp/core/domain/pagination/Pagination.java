package com.vp.core.domain.pagination;

import java.util.List;
import java.util.function.Function;

public record Pagination<T>(
        int currentPage,
        int perPage,
        long total,
        List<T> items
) {

    public static <T> Pagination<T> empty() {
        return new Pagination<>(0, 0, 0, List.of());
    }

    public <R> Pagination<R> map(final Function<T, R> mapper) {
        final List<R> aNewList = this.items.stream()
                .map(mapper)
                .toList();

        return new Pagination<>(currentPage(), perPage(), total(), aNewList);
    }
}
