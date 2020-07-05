package net.boeckling.turbocontainers.modules.mongodb;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;
import org.testcontainers.containers.MongoDBContainer;

public class MongodbModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.registerForContainer(MongoDBContainer.class);
    context.addLifecycleListener(new MongodbStateManager());
    context.addParameterProvider(new MongodbParameterProvider());
  }
}
