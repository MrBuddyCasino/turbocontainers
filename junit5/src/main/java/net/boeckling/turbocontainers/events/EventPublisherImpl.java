package net.boeckling.turbocontainers.events;

import net.boeckling.turbocontainers.modules.ModuleRegistry;
import org.testcontainers.containers.GenericContainer;

public class EventPublisherImpl implements EventPublisher {
  private final ModuleRegistry registry;

  public EventPublisherImpl(ModuleRegistry registry) {
    this.registry = registry;
  }

  public <C extends GenericContainer<?>> void publishEvent(
    ContainerEvent<C> event
  ) {
    LifecycleEvent<C> evt = new LifecycleEventImpl<>(event.getContainer());

    registry
      .getAll()
      .stream()
      .filter(mod -> mod.supportsContainer(event.getContainer()))
      .flatMap(mod -> mod.getLifecycleListeners().stream())
      .forEach(
        listener -> {
          //noinspection unchecked
          LifecycleListener<C> l = (LifecycleListener<C>) listener;
          if (event instanceof AfterContainerInitializedEvent) {
            l.afterContainerInitialized(evt);
          } else {
            if (event instanceof BeforeEachTestEvent) {
              l.beforeEachTest(evt);
            }
          }
        }
      );
  }
}
