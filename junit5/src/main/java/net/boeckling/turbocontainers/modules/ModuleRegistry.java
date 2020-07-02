package net.boeckling.turbocontainers.modules;

import java.util.ArrayList;
import java.util.List;
import net.boeckling.turbocontainers.common.ServiceLoaderUtil;
import net.boeckling.turbocontainers.events.LifecycleListener;
import net.boeckling.turbocontainers.parameter.ParameterProvider;
import org.testcontainers.containers.Container;

public class ModuleRegistry implements SetupContext {
  private final List<LifecycleListener<? extends Container<?>>> lifecycleListeners = new ArrayList<>();
  private final List<ParameterProvider> parameterProviders = new ArrayList<>();

  public ModuleRegistry() {
    registerModules();
  }

  private void registerModules() {
    for (Module module : ServiceLoaderUtil.findServices(Module.class)) {
      module.setupModule(this);
    }
  }

  public List<LifecycleListener<? extends Container<?>>> getLifecycleListeners() {
    return lifecycleListeners;
  }

  public List<ParameterProvider> getParameterProviders() {
    return parameterProviders;
  }

  @Override
  public void addLifecycleListener(LifecycleListener<?> listener) {
    lifecycleListeners.add(listener);
  }

  @Override
  public void addParameterProvider(ParameterProvider paramProvider) {
    parameterProviders.add(paramProvider);
  }
}
