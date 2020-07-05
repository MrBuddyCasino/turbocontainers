package net.boeckling.turbocontainers.parameter;

public interface ParameterProvider {
  boolean supportsParameter(ParameterDescriptor param);

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

    //noinspection unchecked
    return (T) actual;
  }
}
