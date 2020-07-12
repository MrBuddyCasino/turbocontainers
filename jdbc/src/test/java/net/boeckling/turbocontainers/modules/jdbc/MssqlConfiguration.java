package net.boeckling.turbocontainers.modules.jdbc;

import javax.sql.DataSource;
import net.boeckling.turbocontainers.api.init.Init;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.MSSQLServerContainer;

public class MssqlConfiguration {
  public static final MSSQLServerContainer<?> CONTAINER = Init
    .container(
      new MSSQLServerContainer<>(MSSQLServerContainer.IMAGE + ":latest")
    )
    .with(
      ctx ->
        Flyway
          .configure()
          .dataSource(ctx.client(DataSource.class))
          .load()
          .migrate()
    );
}
