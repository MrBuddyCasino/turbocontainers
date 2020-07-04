package net.boeckling.turbocontainers.events;

import net.boeckling.turbocontainers.modules.ModuleRegistry;
import org.testcontainers.containers.Container;

public class EventPublisherImpl implements EventPublisher {
  private final ModuleRegistry registry;

  public EventPublisherImpl(ModuleRegistry registry) {
    this.registry = registry;
  }

  public <C extends Container<?>> void publishEvent(ContainerEvent<C> event) {
    for (LifecycleListener<?> listener : registry.getLifecycleListeners()) {
      if (!listener.supportsContainer(event.getContainer())) {
        continue;
      }

      @SuppressWarnings("unchecked")
      LifecycleListener<C> l = (LifecycleListener<C>) listener;

      LifecycleEvent<C> evt = new LifecycleEventImpl<>(event.getContainer());
      if (event instanceof AfterContainerInitializedEvent) {
        l.afterContainerInitialized(evt);
      } else {
        if (event instanceof BeforeEachTestEvent) {
          l.beforeEachTest(evt);
        }
      }
    }
  }
}
