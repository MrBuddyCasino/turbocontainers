package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import org.testcontainers.containers.OracleContainer;

public class OracleStateTest extends JdbcStateManagerTest {
  @TurboContainer
  private final OracleContainer db = OracleConfiguration.CONTAINER;
}
