package net.boeckling.turbocontainers.state;

import net.boeckling.turbocontainers.events.LifecycleListener;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

/**
 * Init once, take snapshot, restore snapshot.
 */
public interface InitOnceStateManager<C extends GenericContainer<?>>
  extends ToLifecycleListener<C> {
  boolean supportsContainer(Container<?> container);
  void takeSnapshot(C container);
  void restoreSnapshot(C container);

  default LifecycleListener<C> toLifecycleListener() {
    return new InitOnceAdapter<>(this);
  }
}
