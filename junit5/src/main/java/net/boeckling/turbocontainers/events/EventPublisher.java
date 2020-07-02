package net.boeckling.turbocontainers.events;

public interface EventPublisher {
  void publishEvent(ContainerEvent event);
}
