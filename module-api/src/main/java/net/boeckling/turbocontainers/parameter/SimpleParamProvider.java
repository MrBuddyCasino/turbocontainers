package net.boeckling.turbocontainers.parameter;

import org.testcontainers.containers.Container;

@SuppressWarnings("rawtypes")
public abstract class SimpleParamProvider implements ParameterProvider {
  private final Class<?> parameterType;
  private final Class<? extends Container> containerType;

  protected SimpleParamProvider(
    Class<?> parameterType,
    Class<? extends Container> containerType
  ) {
    this.parameterType = parameterType;
    this.containerType = containerType;
  }

  @Override
  public boolean supportsParameter(ParameterDescriptor param) {
    return param.getType().isAssignableFrom(parameterType);
  }

  @Override
  public boolean supportsContainer(Container<?> type) {
    return containerType.isAssignableFrom(type.getClass());
  }
}
