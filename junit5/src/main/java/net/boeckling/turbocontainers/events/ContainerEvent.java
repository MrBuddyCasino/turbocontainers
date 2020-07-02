package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.Container;

public abstract class ContainerEvent {
  private final Container<?> container;

  protected ContainerEvent(Container<?> container) {
    this.container = container;
  }

  public Container<?> getContainer() {
    return container;
  }
}
