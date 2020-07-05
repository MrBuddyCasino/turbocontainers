package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.GenericContainer;

public class AfterContainerInitializedEvent<C extends GenericContainer<?>>
  extends ContainerEvent<C> {

  public AfterContainerInitializedEvent(C container) {
    super(container);
  }
}
