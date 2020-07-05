package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.GenericContainer;

public interface EventPublisher {
  <C extends GenericContainer<?>> void publishEvent(ContainerEvent<C> event);
}
