package com.vp.core.domain;


import com.vp.core.domain.events.DomainEvent;

import java.time.Instant;
import java.util.List;

public abstract class AggregateRoot<ID extends Identifier> extends Entity<ID> {

    protected AggregateRoot(final ID id) {
        super(id);
    }

    protected AggregateRoot(final ID id, Instant createdAt, Instant updatedAt) {
        super(id, createdAt, updatedAt, null);
    }

    protected AggregateRoot(final ID id, Instant createdAt, Instant updatedAt, final List<DomainEvent> events) {
        super(id, createdAt, updatedAt, events);
    }
}
