package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.Container;

public class AfterContainerInitializedEvent extends ContainerEvent {

  public AfterContainerInitializedEvent(Container<?> container) {
    super(container);
  }
}
