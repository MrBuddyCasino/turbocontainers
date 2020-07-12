package net.boeckling.turbocontainers.modules.jdbc;

import javax.sql.DataSource;
import net.boeckling.turbocontainers.api.init.Init;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.MySQLContainer;

public class MysqlConfiguration {
  public static final MySQLContainer<?> CONTAINER = Init
    .container(new MySQLContainer<>(MySQLContainer.IMAGE + ":latest"))
    .with(
      ctx ->
        Flyway
          .configure()
          .dataSource(ctx.client(DataSource.class))
          .load()
          .migrate()
    );
}
