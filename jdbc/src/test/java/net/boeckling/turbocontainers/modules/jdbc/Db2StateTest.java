package net.boeckling.turbocontainers.modules.jdbc;

import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import org.junit.jupiter.api.Disabled;
import org.testcontainers.containers.Db2Container;

@Disabled
public class Db2StateTest extends JdbcStateManagerTest {
  @TurboContainer
  private final Db2Container db = Db2Configuration.CONTAINER;
}
