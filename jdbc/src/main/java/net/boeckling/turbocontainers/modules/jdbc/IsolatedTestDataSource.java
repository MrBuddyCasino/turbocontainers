package net.boeckling.turbocontainers.modules.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * A DataSource handing out connections that can't be closed or committed.
 */
public class IsolatedTestDataSource implements DataSource {
  private final DataSource delegate;

  private final ConcurrentMap<String, Connection> cache = new ConcurrentHashMap<>();

  public IsolatedTestDataSource(DataSource delegate) {
    this.delegate = delegate;
  }

  @Override
  public Connection getConnection() throws SQLException {
    try {
      return cache.computeIfAbsent("default", key -> createNew());
    } catch (RuntimeException e) {
      throw (SQLException) e.getCause();
    }
  }

  @Override
  public Connection getConnection(String username, String password)
    throws SQLException {
    try {
      return cache.computeIfAbsent(
        username + password,
        key -> createNew(username, password)
      );
    } catch (RuntimeException e) {
      throw (SQLException) e.getCause();
    }
  }

  private Connection createNew() {
    try {
      Connection conn = delegate.getConnection();
      conn.setAutoCommit(false);
      return NoCloseConnection.of(NoCommitConnection.of(conn));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Connection createNew(String user, String pass) {
    try {
      Connection conn = delegate.getConnection(user, pass);
      conn.setAutoCommit(false);
      return NoCloseConnection.of(NoCommitConnection.of(conn));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return delegate.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    delegate.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    delegate.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return delegate.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return delegate.getParentLogger();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return delegate.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return delegate.isWrapperFor(iface);
  }
}
