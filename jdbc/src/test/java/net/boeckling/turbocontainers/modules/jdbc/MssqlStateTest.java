package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import org.testcontainers.containers.MSSQLServerContainer;

public class MssqlStateTest extends JdbcStateManagerTest {
  @TurboContainer
  private final MSSQLServerContainer<?> db = MssqlConfiguration.CONTAINER;
}
