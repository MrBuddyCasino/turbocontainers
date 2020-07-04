package net.boeckling.turbocontainers.state;

import net.boeckling.turbocontainers.events.LifecycleListener;
import org.testcontainers.containers.Container;

public interface ToLifecycleListener<C extends Container<?>> {
  LifecycleListener<C> toLifecycleListener();
}
