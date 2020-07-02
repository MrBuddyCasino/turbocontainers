package net.boeckling.turbocontainers.modules.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;

public class NoCloseConnection {

  public static Connection of(Connection delegate) {
    return (Connection) Proxy.newProxyInstance(
      Connection.class.getClassLoader(),
      new Class<?>[] { Connection.class },
      (proxy, method, args) -> {
        String name = method.getName();

        if (name.equals("close") || name.equals("commit")) {
          return null;
        } else if (name.equals("isClosed")) {
          return false;
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
