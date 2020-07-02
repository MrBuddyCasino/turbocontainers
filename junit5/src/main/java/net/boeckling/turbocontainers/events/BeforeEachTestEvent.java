package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.Container;

public class BeforeEachTestEvent extends ContainerEvent {

  public BeforeEachTestEvent(Container<?> container) {
    super(container);
  }
}
