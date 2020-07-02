package net.boeckling.turbocontainers.modules.postgres;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

import java.nio.file.Paths;
import javax.sql.DataSource;
import net.boeckling.turbocontainers.api.init.Init;
import net.boeckling.turbocontainers.modules.cli.Script;
import net.boeckling.turbocontainers.modules.jdbc.DataSourceBiConsumer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresConfigurationAlt {
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
    .with(
      (DataSourceBiConsumer<PostgreSQLContainer<?>>) PostgresConfigurationAlt::initialise
    );

  public static void initialise(
    PostgreSQLContainer<?> container,
    DataSource ds
  ) {
    Script psql = PsqlScript.of(
      Script.of(Paths.get("src/test/resources/psql.sql"))
    );
    psql.runIn(container);
  }
}
