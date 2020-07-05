package net.boeckling.turbocontainers.events;

import net.boeckling.turbocontainers.init.EventEmittingStartupCheckStrategy;
import net.boeckling.turbocontainers.init.InitializingStartupCheckStrategy;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.StartupCheckStrategy;

public class LifecycleEventImpl<C extends GenericContainer<?>>
  implements LifecycleEvent<C> {
  private final C container;

  public LifecycleEventImpl(C container) {
    this.container = container;
  }

  @Override
  public C getContainer() {
    return container;
  }

  @Override
  public Runnable getInitializer() {
    if (container instanceof GenericContainer<?>) {
      GenericContainer<?> c = container;
      StartupCheckStrategy strategy = c.getStartupCheckStrategy();

      if (strategy instanceof EventEmittingStartupCheckStrategy) {
        StartupCheckStrategy delegate =
          ((EventEmittingStartupCheckStrategy) strategy).getDelegate();
        if (delegate instanceof InitializingStartupCheckStrategy) {
          return (
            (InitializingStartupCheckStrategy<?>) delegate
          ).getInitializer();
        }
      }
    }

    return null;
  }
}
