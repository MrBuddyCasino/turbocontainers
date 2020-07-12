package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class MariadbStateTest extends JdbcStateManagerTest {
  @TurboContainer
  private final MariaDBContainer<?> db = MariadbConfiguration.CONTAINER;
}
