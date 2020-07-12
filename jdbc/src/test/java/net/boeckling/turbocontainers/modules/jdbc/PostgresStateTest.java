package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresStateTest extends JdbcStateManagerTest {
  @TurboContainer
  private final PostgreSQLContainer<?> db = PostgresConfiguration.CONTAINER;
}
