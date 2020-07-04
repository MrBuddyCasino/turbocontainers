package net.boeckling.turbocontainers.modules.jdbc;

import javax.sql.DataSource;
import net.boeckling.turbocontainers.parameter.ExecutionEnvironment;
import net.boeckling.turbocontainers.parameter.ParameterDescriptor;
import net.boeckling.turbocontainers.parameter.SimpleParamProvider;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class JdbcParameterProvider extends SimpleParamProvider {
  private DataSource ds;

  public JdbcParameterProvider() {
    super(DataSource.class, JdbcDatabaseContainer.class);
  }

  @Override
  public Object resolveParameter(
    ParameterDescriptor param,
    ExecutionEnvironment env
  ) {
    if (!(env.getContainer() instanceof JdbcDatabaseContainer)) {
      throw new IllegalArgumentException();
    }

    JdbcDatabaseContainer<?> container = tryCast(
      JdbcDatabaseContainer.class,
      env.getContainer()
    );

    if (ds == null) {
      ds = new JdbcContainerDataSource(container);
    }

    if (env.getPhase() == ExecutionEnvironment.Phase.RUN_TEST) {
      return ds = new IsolatedTestDataSource(ds);
    }

    return ds;
  }
}
