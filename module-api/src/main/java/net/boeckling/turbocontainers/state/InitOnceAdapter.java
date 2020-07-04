package net.boeckling.turbocontainers.state;

import net.boeckling.turbocontainers.events.LifecycleEvent;
import net.boeckling.turbocontainers.events.LifecycleListener;
import org.testcontainers.containers.Container;

public class InitOnceAdapter<C extends Container<?>>
  implements LifecycleListener<C> {
  private final InitOnceStateManager<C> stateManager;

  public InitOnceAdapter(InitOnceStateManager<C> stateManager) {
    this.stateManager = stateManager;
  }

  @Override
  public boolean supportsContainer(Container<?> container) {
    return stateManager.supportsContainer(container);
  }

  @Override
  public void afterContainerInitialized(LifecycleEvent<C> event) {
    stateManager.takeSnapshot(event.getContainer());
  }

  @Override
  public void beforeEachTest(LifecycleEvent<C> event) {
    stateManager.restoreSnapshot(event.getContainer());
  }
}
