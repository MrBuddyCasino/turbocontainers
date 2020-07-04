package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.Container;

public abstract class ContainerEvent<C extends Container<?>> {
  private final C container;

  protected ContainerEvent(C container) {
    this.container = container;
  }

  public C getContainer() {
    return container;
  }
}
