package net.boeckling.turbocontainers.modules.postgres;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

@TurboContainers
public class PostgresScriptTest {
  @TurboContainer
  final PostgreSQLContainer<?> container = PostgresConfiguration.CONTAINER;

  @Test
  void postgres_shouldRunPsql_whenStarting() throws SQLException {
    Connection conn = container.createConnection("");
    ResultSet rs = conn
      .prepareStatement(
        "SELECT COUNT(datname) AS cnt FROM pg_database WHERE datname='sample1'"
      )
      .executeQuery();
    rs.next();

    assertThat(rs.getInt("cnt")).isEqualTo(1);
  }
}
