package org.testcontainers.containers;

import org.testcontainers.containers.wait.strategy.WaitStrategy;

public class Accessor {

  public static WaitStrategy getWaitStrategy(GenericContainer<?> delegate) {
    return delegate.getWaitStrategy();
  }
}
