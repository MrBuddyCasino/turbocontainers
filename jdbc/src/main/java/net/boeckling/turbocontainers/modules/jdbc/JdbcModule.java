package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;

public class JdbcModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.addLifecycleListener(new JdbcStateManager());
    context.addParameterProvider(new JdbcParameterProvider());
  }
}
