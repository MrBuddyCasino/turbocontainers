package net.boeckling.turbocontainers.modules.mongodb;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;

public class MongodbModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.addLifecycleListener(new MongodbStateManager());
    context.addParameterProvider(new MongodbParamProvider());
  }
}
