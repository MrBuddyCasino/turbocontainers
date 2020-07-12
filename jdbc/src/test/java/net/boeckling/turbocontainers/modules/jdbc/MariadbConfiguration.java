package net.boeckling.turbocontainers.modules.jdbc;

import javax.sql.DataSource;
import net.boeckling.turbocontainers.api.init.Init;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.MariaDBContainer;

public class MariadbConfiguration {
  public static final MariaDBContainer<?> CONTAINER = Init
    .container(new MariaDBContainer<>(MariaDBContainer.IMAGE + ":latest"))
    .with(
      ctx ->
        Flyway
          .configure()
          .dataSource(ctx.client(DataSource.class))
          .load()
          .migrate()
    );
}
