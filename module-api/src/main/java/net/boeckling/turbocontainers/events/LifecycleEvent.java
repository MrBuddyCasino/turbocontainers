package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.GenericContainer;

public interface LifecycleEvent<C extends GenericContainer> {
  C getContainer();
  Runnable getInitializer();
}
