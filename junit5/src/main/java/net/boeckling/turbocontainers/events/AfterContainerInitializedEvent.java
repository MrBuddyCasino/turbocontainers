package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.Container;

public class AfterContainerInitializedEvent<C extends Container<?>>
  extends ContainerEvent<C> {

  public AfterContainerInitializedEvent(C container) {
    super(container);
  }
}
