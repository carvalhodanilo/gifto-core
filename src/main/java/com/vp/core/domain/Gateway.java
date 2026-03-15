package com.vp.core.domain;

import com.vp.core.domain.pagination.Pagination;
import com.vp.core.domain.pagination.SearchQuery;

import java.util.Optional;

public interface Gateway<T, ID extends Identifier> {

    T create(T aT);

    void deleteById(ID anId);

    Optional<T> findById(ID anId);

    T update(T aT);

    Pagination<T> findAll(SearchQuery aQuery);
}
