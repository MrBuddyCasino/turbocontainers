package net.boeckling.turbocontainers.events;

import java.util.function.Consumer;
import org.testcontainers.containers.Container;

public interface LifecycleListener<C extends Container<?>> {
  boolean supportsContainer(Container<?> container);

  /**
   * Opportunity to initialize.
   */
  default void beforeContainerInitialized(LifecycleEvent<C> event) {}

  /**
   * Opportunity to eg take snapshots to avoid re-initialization.
   */
  default void afterContainerInitialized(LifecycleEvent<C> event) {}

  /**
   * Reset container state.
   */
  default void beforeEachTest(LifecycleEvent<C> event) {}

  /**
   * Creates an init-only listener.
   */
  static <C extends Container<?>> LifecycleListener<C> of(
    Class<?> supportedType,
    Consumer<C> initializer
  ) {
    return new LifecycleListener<C>() {

      @Override
      public boolean supportsContainer(Container<?> container) {
        return supportedType.isInstance(container);
      }

      @Override
      public void beforeContainerInitialized(LifecycleEvent<C> event) {
        initializer.accept(event.getContainer());
      }
    };
  }
}
