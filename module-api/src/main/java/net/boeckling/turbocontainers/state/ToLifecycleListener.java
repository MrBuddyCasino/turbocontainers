package net.boeckling.turbocontainers.state;

import net.boeckling.turbocontainers.events.LifecycleListener;
import org.testcontainers.containers.GenericContainer;

public interface ToLifecycleListener<C extends GenericContainer<?>> {
  LifecycleListener<C> toLifecycleListener();
}
