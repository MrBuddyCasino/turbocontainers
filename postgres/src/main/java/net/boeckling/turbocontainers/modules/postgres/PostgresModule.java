package net.boeckling.turbocontainers.modules.postgres;

import net.boeckling.turbocontainers.modules.Module;
import net.boeckling.turbocontainers.modules.SetupContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresModule implements Module {

  @Override
  public void setupModule(SetupContext context) {
    context.registerForContainer(PostgreSQLContainer.class);
    context.registerScriptRunner(new PostgresScriptRunner());
  }
}
