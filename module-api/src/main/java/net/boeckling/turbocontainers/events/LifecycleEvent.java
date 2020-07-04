package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.Container;

public interface LifecycleEvent<C extends Container<?>> {
  C getContainer();
  Runnable getInitializer();
}
