package net.boeckling.turbocontainers.modules.cassandra;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;
import org.testcontainers.containers.CassandraContainer;

public class CassandraModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.registerForContainer(CassandraContainer.class);
    context.addParameterProvider(new CassandraParameterProvider());
    context.addLifecycleListener(new CassandraStateManager());
    context.registerScriptRunner(new CassandraScriptRunner());
  }
}
