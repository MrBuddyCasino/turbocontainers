package net.boeckling.turbocontainers.modules.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

@TurboContainers
public abstract class JdbcStateManagerTest {

  @Test
  void database_shouldRunInitialization_whenStarting(DataSource ds)
    throws SQLException {
    String query = "SELECT COUNT(*) AS cnt FROM books";
    ResultSet rs = query(ds, query);
    assertThat(rs.getInt("cnt")).isEqualTo(1);
  }

  @Test
  @Order(1)
  void dataSource_shouldKeepDataVisible_WhenWithinTest(DataSource ds)
    throws SQLException {
    Connection conn = ds.getConnection();
    conn.setAutoCommit(true); // this shouldn't work
    conn
      .prepareStatement(
        "INSERT INTO books (year, name) VALUES (2012, 'Antifragile')"
      )
      .execute();
    conn.commit(); // this neither

    String query = "SELECT COUNT(*) AS cnt FROM books";
    ResultSet rs = query(ds, query);
    assertThat(rs.getInt("cnt")).isEqualTo(2);
  }

  @Test
  @Order(2)
  void dataSource_shouldDiscardDataFromPreviousTest_whenDbWasMutated(
    DataSource ds
  )
    throws SQLException {
    String query = "SELECT COUNT(*) AS cnt FROM books";
    ResultSet rs = query(ds, query);
    assertThat(rs.getInt("cnt")).isEqualTo(1);
  }

  private ResultSet query(DataSource ds, String sql) throws SQLException {
    Connection conn = ds.getConnection();
    ResultSet rs = conn.prepareStatement(sql).executeQuery();
    rs.next();
    return rs;
  }
}
