package net.boeckling.turbocontainers.modules.localstack;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;

public class LocalstackModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.addLifecycleListener(new LocalstackStateManager());
    context.addParameterProvider(new LocalstackParameterProvider());
  }
}
