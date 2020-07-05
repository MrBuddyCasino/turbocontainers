package org.testcontainers.containers;

import org.testcontainers.delegate.DatabaseDelegate;

public class JdbcContainerAccessor {

  public static DatabaseDelegate getDatabaseDelegate(
    JdbcDatabaseContainer<?> container
  ) {
    return container.getDatabaseDelegate();
  }
}
