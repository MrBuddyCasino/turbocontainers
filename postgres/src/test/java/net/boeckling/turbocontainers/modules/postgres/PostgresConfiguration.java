package net.boeckling.turbocontainers.modules.postgres;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

import net.boeckling.turbocontainers.api.init.Init;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresConfiguration {
  public static final PostgreSQLContainer<?> CONTAINER = Init
    .container(
      new PostgreSQLContainer<>(PostgreSQLContainer.IMAGE + ":11.6")
        .withNetwork(Network.SHARED)
        .withNetworkAliases("postgres")
        .withExposedPorts(POSTGRESQL_PORT)
        .withDatabaseName("postgres")
        .withUsername("postgres")
        .withPassword("postgres")
    )
    .with(ctx -> ctx.initScript("psql.sql"));
}
