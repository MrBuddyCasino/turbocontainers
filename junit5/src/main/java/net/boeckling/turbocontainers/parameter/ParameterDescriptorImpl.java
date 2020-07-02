package net.boeckling.turbocontainers.parameter;

public class ParameterDescriptorImpl implements ParameterDescriptor {
  private final Class<?> type;

  public ParameterDescriptorImpl(Class<?> type) {
    this.type = type;
  }

  @Override
  public Class<?> getType() {
    return type;
  }
}
