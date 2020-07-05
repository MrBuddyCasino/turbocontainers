package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.GenericContainer;

public abstract class ContainerEvent<C extends GenericContainer<?>> {
  private final C container;

  protected ContainerEvent(C container) {
    this.container = container;
  }

  public C getContainer() {
    return container;
  }
}
