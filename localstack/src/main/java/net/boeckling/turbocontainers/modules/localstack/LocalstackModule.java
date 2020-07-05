package net.boeckling.turbocontainers.modules.localstack;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;
import org.testcontainers.containers.localstack.LocalStackContainer;

public class LocalstackModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.registerForContainer(LocalStackContainer.class);
    context.addLifecycleListener(new LocalstackStateManager());
    context.addParameterProvider(new LocalstackParameterProvider());
  }
}
