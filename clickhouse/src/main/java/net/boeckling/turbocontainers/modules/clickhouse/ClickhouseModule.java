package net.boeckling.turbocontainers.modules.clickhouse;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;

public class ClickhouseModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.addLifecycleListener(new ClickhouseStateManager());
  }
}
