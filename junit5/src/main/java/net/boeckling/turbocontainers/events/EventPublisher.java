package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.Container;

public interface EventPublisher {
  <C extends Container<?>> void publishEvent(ContainerEvent<C> event);
}
