package com.vp.core.domain;

import com.vp.core.domain.events.DomainEvent;
import com.vp.core.domain.events.DomainEventPublisher;
import com.vp.core.domain.utils.InstantUtils;
import com.vp.core.domain.validation.ValidationHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Entity<ID extends Identifier> {

    protected final ID id;
    protected Instant createdAt;
    protected Instant updatedAt;

    private final List<DomainEvent> domainEvents;

    protected Entity(ID id) {
        Objects.requireNonNull(id, "'id' should not be null");
        this.id = id;
        this.createdAt = InstantUtils.now();
        this.updatedAt = InstantUtils.now();
        this.domainEvents = new ArrayList<>();
    }

    protected Entity(ID id, Instant createdAt, Instant updatedAt, final List<DomainEvent> domainEvents) {
        Objects.requireNonNull(id, "'id' should not be null");
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.domainEvents = new ArrayList<>(domainEvents == null ? Collections.emptyList() : domainEvents);
    }

    protected void touch() {
        this.updatedAt = InstantUtils.now();
    }

    public abstract void validate(ValidationHandler handler);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(getId(), entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void publishDomainEvents(final DomainEventPublisher publisher) {
        if (publisher == null) {
            return;
        }

        getDomainEvents()
                .forEach(publisher::publishEvent);

        this.domainEvents.clear();
    }

    public void registerEvent(final DomainEvent event) {
        if (event == null) {
            return;
        }

        this.domainEvents.add(event);
    }

    public ID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
