package net.boeckling.turbocontainers.modules.cassandra;

import net.boeckling.turbocontainers.modules.cli.Script;

public class CqlshScript {
  public static final Script CQLSH = Script.of("cqlsh");

  public static Script of(Script script) {
    String commands = "--execute=" + script.joinCommands("");
    return CQLSH.concat(Script.of(commands));
  }
}
