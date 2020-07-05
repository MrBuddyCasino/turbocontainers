package net.boeckling.turbocontainers.state;

import net.boeckling.turbocontainers.events.LifecycleListener;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

/**
 * Wipe completely, then re-init.
 */
public interface InitAlwaysStateManager<C extends GenericContainer<?>>
  extends ToLifecycleListener<C> {
  boolean supportsContainer(Container<?> container);

  void wipe(C container);

  default LifecycleListener<C> toLifecycleListener() {
    return new InitAlwaysAdapter<>(this);
  }
}
