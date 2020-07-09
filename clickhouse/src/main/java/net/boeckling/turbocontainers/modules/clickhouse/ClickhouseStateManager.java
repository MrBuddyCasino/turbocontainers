package net.boeckling.turbocontainers.modules.clickhouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.boeckling.turbocontainers.state.InitAlwaysStateManager;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.containers.Container;

public class ClickhouseStateManager
  implements InitAlwaysStateManager<ClickHouseContainer> {

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof ClickHouseContainer;
  }

  @Override
  public void wipe(ClickHouseContainer container) {
    try {
      Connection conn = container
        .getJdbcDriverInstance()
        .connect(container.getJdbcUrl(), null);

      ResultSet rs = conn.prepareStatement("SHOW DATABASES").executeQuery();

      List<String> databases = new ArrayList<>();
      while (rs.next()) {
        String name = rs.getString("name");
        databases.add(name);
      }

      databases
        .stream()
        .filter(db -> !db.equals("system"))
        .forEach(
          db -> {
            try {
              PreparedStatement stmt = conn.prepareStatement("DROP DATABASE ?");
              stmt.setString(1, db);
              stmt.execute();
            } catch (SQLException e) {
              throw new RuntimeException(e);
            }
          }
        );
      conn.prepareStatement("CREATE DATABASE default").execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
