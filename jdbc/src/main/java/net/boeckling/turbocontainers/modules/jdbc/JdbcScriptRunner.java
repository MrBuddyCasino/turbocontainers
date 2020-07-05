package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.script.ScriptRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcContainerAccessor;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.delegate.DatabaseDelegate;
import org.testcontainers.ext.ScriptUtils;

public class JdbcScriptRunner implements ScriptRunner {

  @Override
  public void runScript(GenericContainer<?> container, String initScriptPath) {
    DatabaseDelegate databaseDelegate = JdbcContainerAccessor.getDatabaseDelegate(
      (JdbcDatabaseContainer<?>) container
    );
    ScriptUtils.runInitScript(databaseDelegate, initScriptPath);
  }
}
