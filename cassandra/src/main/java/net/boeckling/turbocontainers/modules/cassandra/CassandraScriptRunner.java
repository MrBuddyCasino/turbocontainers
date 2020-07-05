package net.boeckling.turbocontainers.modules.cassandra;

import javax.script.ScriptException;
import net.boeckling.turbocontainers.script.AbstractScriptRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.delegate.CassandraDatabaseDelegate;
import org.testcontainers.delegate.DatabaseDelegate;
import org.testcontainers.ext.ScriptUtils;

public class CassandraScriptRunner extends AbstractScriptRunner {

  @Override
  protected void execute(
    GenericContainer<?> container,
    String initScriptPath,
    String initScript
  )
    throws ScriptException {
    DatabaseDelegate delegate = new CassandraDatabaseDelegate(container);
    ScriptUtils.executeDatabaseScript(delegate, initScriptPath, initScript);
  }
}
