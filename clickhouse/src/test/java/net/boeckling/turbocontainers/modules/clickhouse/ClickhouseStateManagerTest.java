package net.boeckling.turbocontainers.modules.clickhouse;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ClickHouseContainer;

@TurboContainers
public class ClickhouseStateManagerTest {
  @TurboContainer
  final ClickHouseContainer container = ClickhouseConfiguration.CONTAINER;

  @Test
  @Order(1)
  void module_shouldInitializeContainer_onStartup(DataSource ds)
    throws SQLException {
    Connection conn = ds.getConnection();

    ResultSet rs = conn.prepareStatement("SHOW DATABASES").executeQuery();
    List<String> databases = collect(rs, r -> r.getString("name"));

    assertThat(databases).hasSize(3);
    assertThat(databases).contains("db1");

    rs = conn.prepareStatement("SELECT * FROM db1.books").executeQuery();
    List<String> titles = collect(rs, r -> r.getString("title"));

    assertThat(titles).hasSize(1);
    assertThat(titles).contains("The Power Broker");

    conn.prepareStatement("CREATE DATABASE some_db").execute();
    conn
      .prepareStatement(
        "INSERT INTO db1.books (event_id, title) VALUES (2, 'Meditations')"
      )
      .execute();
  }

  @Test
  @Order(2)
  void module_shouldResetState_betweenTests(DataSource ds) throws SQLException {
    Connection conn = ds.getConnection();

    ResultSet rs = conn.prepareStatement("SHOW DATABASES").executeQuery();
    List<String> databases = collect(rs, r -> r.getString("name"));
    assertThat(databases).hasSize(3);
    assertThat(databases).doesNotContain("some_db");

    rs = conn.prepareStatement("SELECT * FROM db1.books").executeQuery();
    List<String> titles = collect(rs, r -> r.getString("title"));

    assertThat(titles).hasSize(1);
    assertThat(titles).doesNotContain("Meditations");
  }

  private <T> List<T> collect(ResultSet rs, SqlMapper<ResultSet, T> mapper)
    throws SQLException {
    List<T> databases = new ArrayList<>();
    while (rs.next()) {
      databases.add(mapper.apply(rs));
    }
    return databases;
  }
}
