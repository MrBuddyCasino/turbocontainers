package net.boeckling.turbocontainers.parameter;

import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

public class ExecutionEnvironmentImpl implements ExecutionEnvironment {
  private final GenericContainer<?> container;
  private final Phase phase;

  public ExecutionEnvironmentImpl(
    GenericContainer<?> genericContainer,
    Phase phase
  ) {
    this.container = genericContainer;
    this.phase = phase;
  }

  @Override
  public Container<?> getContainer() {
    return container;
  }

  @Override
  public Phase getPhase() {
    return phase;
  }
}
