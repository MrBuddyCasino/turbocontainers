package net.boeckling.turbocontainers.modules.jdbc;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import net.boeckling.turbocontainers.api.init.Init;
import org.testcontainers.containers.PrestoContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

public class PrestoConfiguration {
  // bugfix for broken wait strategy
  private static final WaitStrategy WAIT_STRATEGY = new LogMessageWaitStrategy()
    .withRegEx(".*======== SERVER STARTED ========.*")
    .withStartupTimeout(Duration.of(60, SECONDS));

  public static final PrestoContainer<?> CONTAINER = Init
    .container(createContainer())
    .with(
      ctx -> {
        try {
          // avoid "No nodes available to run the query" error
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        ctx.initScript("db/migration/V1__init.sql");
      }
    );

  private static PrestoContainer<?> createContainer() {
    PrestoContainer<?> presto = new PrestoContainer<>(
      PrestoContainer.IMAGE + ":latest"
    )
      .withDatabaseName("memory")
      .withCommand(
        "/usr/lib/presto/bin/run-presto",
        "-Dsql.default-catalog=memory",
        "-Dsql.default-schema=default"
      );
    presto.setWaitStrategy(WAIT_STRATEGY);
    return presto;
  }
}
