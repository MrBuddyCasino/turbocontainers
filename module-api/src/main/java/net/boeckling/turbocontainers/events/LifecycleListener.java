package net.boeckling.turbocontainers.events;

import java.util.function.Consumer;
import org.testcontainers.containers.Container;

public interface LifecycleListener<C extends Container<?>> {
  boolean supportsContainer(Container<?> container);

  /**
   * Opportunity to initialize.
   */
  default void beforeContainerInitialized(C container) {}

  default void afterContainerInitialized(C container) {}

  default void beforeEachTest(C container) {}

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
      public void beforeContainerInitialized(C container) {
        initializer.accept(container);
      }
    };
  }
}
