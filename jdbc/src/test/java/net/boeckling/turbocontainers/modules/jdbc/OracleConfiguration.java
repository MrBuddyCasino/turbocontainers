package net.boeckling.turbocontainers.modules.jdbc;

import javax.sql.DataSource;
import net.boeckling.turbocontainers.api.init.Init;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.OracleContainer;

public class OracleConfiguration {
  public static final OracleContainer CONTAINER = Init
    .container(new OracleContainer("pvargacl/oracle-xe-18.4.0:latest"))
    .with(
      ctx ->
        Flyway
          .configure()
          .dataSource(ctx.client(DataSource.class))
          .load()
          .migrate()
    );
}
