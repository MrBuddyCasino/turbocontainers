package net.boeckling.turbocontainers.modules.jdbc;

import java.util.function.BiConsumer;
import javax.sql.DataSource;

public interface DataSourceBiConsumer<C> extends BiConsumer<C, DataSource> {}
