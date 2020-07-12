package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import org.testcontainers.containers.PrestoContainer;

public class PrestoStateTest extends JdbcStateManagerTest {
  @TurboContainer
  private final PrestoContainer<?> db = PrestoConfiguration.CONTAINER;
}
