package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class JdbcModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.registerForContainer(JdbcDatabaseContainer.class);
    context.addLifecycleListener(new JdbcStateManager());
    context.addParameterProvider(new JdbcParameterProvider());
    context.registerScriptRunner(new JdbcScriptRunner());
  }
}
