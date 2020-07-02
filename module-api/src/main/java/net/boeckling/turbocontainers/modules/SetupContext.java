package net.boeckling.turbocontainers.modules;

import net.boeckling.turbocontainers.events.LifecycleListener;
import net.boeckling.turbocontainers.parameter.ParameterProvider;

public interface SetupContext {
  void addLifecycleListener(LifecycleListener<?> listener);
  void addParameterProvider(ParameterProvider paramProvider);
}
