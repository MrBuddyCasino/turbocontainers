package net.boeckling.turbocontainers.events;

import org.testcontainers.containers.GenericContainer;

public class BeforeEachTestEvent<C extends GenericContainer<?>>
  extends ContainerEvent<C> {

  public BeforeEachTestEvent(C container) {
    super(container);
  }
}
