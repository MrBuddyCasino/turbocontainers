package net.boeckling.turbocontainers.state;

import net.boeckling.turbocontainers.events.LifecycleEvent;
import net.boeckling.turbocontainers.events.LifecycleListener;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

public class InitAlwaysAdapter<C extends GenericContainer<?>>
  implements LifecycleListener<C> {
  private final InitAlwaysStateManager<C> stateManager;

  public InitAlwaysAdapter(InitAlwaysStateManager<C> stateManager) {
    this.stateManager = stateManager;
  }

  @Override
  public boolean supportsContainer(Container<?> container) {
    return stateManager.supportsContainer(container);
  }

  @Override
  public void beforeEachTest(LifecycleEvent<C> event) {
    stateManager.wipe(event.getContainer());
    event.getInitializer().run();
  }
}
