package net.boeckling.turbocontainers.modules.postgres;

import net.boeckling.turbocontainers.modules.cli.Script;
import net.boeckling.turbocontainers.script.AbstractScriptRunner;
import org.testcontainers.containers.GenericContainer;

public class PostgresScriptRunner extends AbstractScriptRunner {

  @Override
  public int getPriority() {
    return Integer.MAX_VALUE;
  }

  @Override
  protected void execute(
    GenericContainer<?> container,
    String initScriptPath,
    String initScript
  ) {
    PsqlScript.of(Script.of(initScript)).runIn(container);
  }
}
