package net.boeckling.turbocontainers.parameter;

import org.testcontainers.containers.Container;

public interface ParameterProvider {
  boolean supportsParameter(ParameterDescriptor param);
  boolean supportsContainer(Container<?> type);

  Object resolveParameter(ParameterDescriptor param, ExecutionEnvironment env)
    throws ParameterResolutionFailedException;

  default <T> T tryCast(Class<T> required, Object actual) {
    if (!(required.isInstance(actual))) {
      throw new IllegalArgumentException(
        "type check failed, got " +
        actual.getClass() +
        ", need " +
        required.getName()
      );
    }

    return (T) actual;
  }
}
