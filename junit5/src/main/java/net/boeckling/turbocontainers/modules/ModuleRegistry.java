package net.boeckling.turbocontainers.modules;

import java.util.*;
import net.boeckling.turbocontainers.common.ServiceLoaderUtil;
import net.boeckling.turbocontainers.parameter.ParameterDescriptor;
import org.testcontainers.containers.GenericContainer;

public class ModuleRegistry {
  private final Map<Class<?>, RegisteredModule> containerToModule = new HashMap<>();

  public ModuleRegistry() {
    registerModules();
  }

  private void registerModules() {
    for (Module module : ServiceLoaderUtil.findServices(Module.class)) {
      RegisteredModule registeredModule = new RegisteredModule();
      module.setupModule(registeredModule);
      for (Class<?> supportedClass : registeredModule.getSupportedClasses()) {
        containerToModule.put(supportedClass, registeredModule);
      }
    }
  }

  public Optional<RegisteredModule> findModule(GenericContainer<?> container) {
    for (Map.Entry<Class<?>, RegisteredModule> entry : containerToModule.entrySet()) {
      if (entry.getKey().isInstance(container)) {
        return Optional.of(entry.getValue());
      }
    }

    return Optional.empty();
  }

  public Optional<RegisteredModule> findModuleForParameterType(
    ParameterDescriptor desc
  ) {
    return containerToModule
      .values()
      .stream()
      .filter(
        mod ->
          mod
            .getParameterProviders()
            .stream()
            .anyMatch(prov -> prov.supportsParameter(desc))
      )
      .findFirst();
  }

  public Collection<RegisteredModule> getAll() {
    return containerToModule.values();
  }
}
