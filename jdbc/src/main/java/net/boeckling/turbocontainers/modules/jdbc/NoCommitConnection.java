package net.boeckling.turbocontainers.modules.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;

public class NoCommitConnection {

  public static Connection of(Connection delegate) {
    return (Connection) Proxy.newProxyInstance(
      Connection.class.getClassLoader(),
      new Class<?>[] { Connection.class },
      (proxy, method, args) -> {
        String name = method.getName();

        if (name.equals("commit") || name.equals("setAutoCommit")) {
          return null;
        }

        try {
          return method.invoke(delegate, args);
        } catch (InvocationTargetException ex) {
          throw ex.getTargetException();
        }
      }
    );
  }
}
