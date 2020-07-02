package net.boeckling.turbocontainers.modules.postgres;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

import java.nio.file.Paths;
import javax.sql.DataSource;
import net.boeckling.turbocontainers.api.init.Init;
import net.boeckling.turbocontainers.modules.cli.Script;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresConfiguration {
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
      (con, ds) ->
        PsqlScript
          .of(Script.of(Paths.get("src/test/resources/psql.sql")))
          .runIn(con)
    );
}
