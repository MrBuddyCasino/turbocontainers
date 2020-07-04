package net.boeckling.turbocontainers.modules.jdbc;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

import java.util.function.BiConsumer;
import javax.sql.DataSource;
import net.boeckling.turbocontainers.api.init.Init;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresConfiguration
  implements BiConsumer<PostgreSQLContainer<?>, DataSource> {
  public static final PostgreSQLContainer<?> CONTAINER = Init
    .<PostgreSQLContainer<?>, DataSource>container(
      new PostgreSQLContainer<>(PostgreSQLContainer.IMAGE + ":11.6")
        .withNetwork(Network.SHARED)
        .withNetworkAliases("postgres")
        .withExposedPorts(POSTGRESQL_PORT)
        .withDatabaseName("postgres")
        .withUsername("postgres")
        .withPassword("postgres")
    )
    .with(new PostgresConfiguration());

  @Override
  public void accept(
    PostgreSQLContainer<?> postgreSQLContainer,
    DataSource dataSource
  ) {
    Flyway.configure().dataSource(dataSource).load().migrate();
  }
}
