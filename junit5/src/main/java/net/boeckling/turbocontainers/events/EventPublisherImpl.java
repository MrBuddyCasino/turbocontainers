package net.boeckling.turbocontainers.events;

import net.boeckling.turbocontainers.modules.ModuleRegistry;

public class EventPublisherImpl implements EventPublisher {
  private final ModuleRegistry registry;

  public EventPublisherImpl(ModuleRegistry registry) {
    this.registry = registry;
  }

  public void publishEvent(ContainerEvent event) {
    for (LifecycleListener listener : registry.getLifecycleListeners()) {
      if (!listener.supportsContainer(event.getContainer())) {
        continue;
      }

      if (event instanceof AfterContainerInitializedEvent) {
        listener.afterContainerInitialized(event.getContainer());
      } else {
        if (event instanceof BeforeEachTestEvent) {
          listener.beforeEachTest(event.getContainer());
        }
      }
    }
  }
}
