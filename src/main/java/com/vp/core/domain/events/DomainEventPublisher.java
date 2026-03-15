package com.vp.core.domain.events;

@FunctionalInterface
public interface DomainEventPublisher {
    void publishEvent(DomainEvent event);
}
