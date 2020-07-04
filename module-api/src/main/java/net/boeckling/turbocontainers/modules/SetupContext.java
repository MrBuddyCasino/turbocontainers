package net.boeckling.turbocontainers.modules;

import net.boeckling.turbocontainers.events.LifecycleListener;
import net.boeckling.turbocontainers.parameter.ParameterProvider;
import net.boeckling.turbocontainers.state.ToLifecycleListener;

public interface SetupContext {
  void addLifecycleListener(LifecycleListener<?> listener);

  default void addLifecycleListener(ToLifecycleListener<?> toListener) {
    addLifecycleListener(toListener.toLifecycleListener());
  }

  void addParameterProvider(ParameterProvider paramProvider);
}
