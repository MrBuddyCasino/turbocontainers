package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.Container;

@FunctionalInterface
public interface AfterContainerInitializedListener<C extends Container<?>> {
  void afterContainerInitialized(C container);
}
