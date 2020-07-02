package net.boeckling.turbocontainers.modules.postgres;

import net.boeckling.turbocontainers.modules.cli.Script;
import net.boeckling.turbocontainers.modules.cli.ShellScript;

public class PsqlScript {

  public static Script of(Script script) {
    Script psql = Script.of(wrap(script.joinCommands("\n")));
    return ShellScript.of(psql); // run via shell, so env. var expansion etc. works
  }

  private static String wrap(String script) {
    return (
      "/usr/bin/psql" +
      " -v ON_ERROR_STOP=1 --username \"$POSTGRES_USER\" --no-password --dbname \"$POSTGRES_DB\"" +
      "<<-'EOSQL'\n" +
      script +
      "\nEOSQL"
    );
  }
}
