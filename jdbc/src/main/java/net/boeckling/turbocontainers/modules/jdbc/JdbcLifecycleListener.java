package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.events.LifecycleListener;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class JdbcLifecycleListener
  implements LifecycleListener<JdbcDatabaseContainer<?>> {

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof JdbcDatabaseContainer<?>;
  }
}
