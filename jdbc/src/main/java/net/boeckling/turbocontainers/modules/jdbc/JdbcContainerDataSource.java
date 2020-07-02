package net.boeckling.turbocontainers.modules.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;

/**
 * A simple non-pooling {@link DataSource} that ceates connections via
 * {@link JdbcDatabaseContainer#createConnection(String)}.
 */
public class JdbcContainerDataSource implements DataSource {
  private final JdbcDatabaseContainer<?> container;

  public JdbcContainerDataSource(JdbcDatabaseContainer<?> container) {
    this.container = container;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return container.createConnection("");
  }

  @Override
  public Connection getConnection(String username, String password)
    throws SQLException {
    return container.createConnection("");
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> iface) throws SQLException {
    if (iface.isInstance(this)) {
      return (T) this;
    }
    throw new SQLException(
      "DataSource of type [" +
      getClass().getName() +
      "] cannot be unwrapped as [" +
      iface.getName() +
      "]"
    );
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) {
    return iface.isInstance(this);
  }

  @Override
  public PrintWriter getLogWriter() {
    throw new UnsupportedOperationException("getLogWriter()");
  }

  @Override
  public void setLogWriter(PrintWriter out) {
    throw new UnsupportedOperationException("setLogWriter()");
  }

  @Override
  public void setLoginTimeout(int seconds) {
    throw new UnsupportedOperationException("setLoginTimeout()");
  }

  @Override
  public int getLoginTimeout() {
    return 0;
  }

  @Override
  public Logger getParentLogger() {
    return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  }
}
