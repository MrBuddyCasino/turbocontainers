package net.boeckling.turbocontainers.modules.clickhouse;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlMapper<T, R> {
  /**
   * Applies this function to the given argument.
   *
   * @param t the function argument
   * @return the function result
   */
  R apply(T t) throws SQLException;
}
