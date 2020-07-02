package net.boeckling.turbocontainers.modules.localstack;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;
import net.boeckling.turbocontainers.parameter.ParameterProvider;

public class LocalstackModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.addLifecycleListener(new LocalstackLifecycleListener());
    context.addParameterProvider(new LocalstackParameterProvider());
  }
}
