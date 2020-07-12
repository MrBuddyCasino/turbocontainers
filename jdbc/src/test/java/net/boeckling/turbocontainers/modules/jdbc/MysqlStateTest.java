package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import org.testcontainers.containers.MySQLContainer;

public class MysqlStateTest extends JdbcStateManagerTest {
  @TurboContainer
  private final MySQLContainer<?> db = MysqlConfiguration.CONTAINER;
}
