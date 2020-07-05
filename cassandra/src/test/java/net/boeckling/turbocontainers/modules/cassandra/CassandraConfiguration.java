package net.boeckling.turbocontainers.modules.cassandra;

import net.boeckling.turbocontainers.api.init.Init;
import org.testcontainers.containers.CassandraContainer;

public class CassandraConfiguration {
  static final CassandraContainer<?> CONTAINER = Init
    .container(new CassandraContainer<>())
    .with(ctx -> ctx.initScript("init.cql"));
}
