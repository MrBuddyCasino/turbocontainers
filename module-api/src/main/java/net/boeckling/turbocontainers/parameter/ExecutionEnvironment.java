package net.boeckling.turbocontainers.parameter;

import org.testcontainers.containers.Container;

public interface ExecutionEnvironment {
  Container<?> getContainer();
  Phase getPhase();

  enum Phase {
    INIT_CONTAINER,
    RUN_TEST,
  }
}
