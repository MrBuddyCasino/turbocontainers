package net.boeckling.turbocontainers.modules.clickhouse;

import net.boeckling.turbocontainers.api.init.Init;
import org.testcontainers.containers.ClickHouseContainer;

public class ClickhouseConfiguration {
  static final ClickHouseContainer CONTAINER = Init
    .container(new ClickHouseContainer())
    .with(ctx -> ctx.initScript("init.sql"));
}
