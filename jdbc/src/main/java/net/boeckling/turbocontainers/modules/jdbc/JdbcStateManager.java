package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.state.InitOnceStateManager;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class JdbcStateManager
  implements InitOnceStateManager<JdbcDatabaseContainer<?>> {

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof JdbcDatabaseContainer<?>;
  }

  @Override
  public void takeSnapshot(JdbcDatabaseContainer<?> container) {}

  @Override
  public void restoreSnapshot(JdbcDatabaseContainer<?> container) {}
}
