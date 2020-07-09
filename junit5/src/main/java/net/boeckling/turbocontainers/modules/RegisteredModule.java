package net.boeckling.turbocontainers.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.boeckling.turbocontainers.events.LifecycleListener;
import net.boeckling.turbocontainers.parameter.ParameterProvider;
import net.boeckling.turbocontainers.script.ScriptRunner;
import org.testcontainers.containers.GenericContainer;

public class RegisteredModule implements SetupContext {
  private final List<LifecycleListener<? extends GenericContainer>> lifecycleListeners = new ArrayList<>();
  private final List<ParameterProvider> parameterProviders = new ArrayList<>();
  private final List<Class<?>> supportedClasses = new ArrayList<>();
  private ScriptRunner scriptRunner;

  public List<LifecycleListener<? extends GenericContainer>> getLifecycleListeners() {
    return lifecycleListeners;
  }

  public List<ParameterProvider> getParameterProviders() {
    return parameterProviders;
  }

  public List<Class<?>> getSupportedClasses() {
    return supportedClasses;
  }

  public Optional<ScriptRunner> getScriptRunner() {
    return Optional.ofNullable(scriptRunner);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void registerForContainer(Class<?>... container) {
    Collections.addAll(supportedClasses, container);
  }

  public boolean supportsContainer(GenericContainer<?> container) {
    return supportedClasses.stream().anyMatch(c -> c.isInstance(container));
  }

  @Override
  public void addLifecycleListener(LifecycleListener<?> listener) {
    lifecycleListeners.add(listener);
  }

  @Override
  public void addParameterProvider(ParameterProvider paramProvider) {
    parameterProviders.add(paramProvider);
  }

  @Override
  public void registerScriptRunner(ScriptRunner scriptRunner) {
    this.scriptRunner = scriptRunner;
  }
}
