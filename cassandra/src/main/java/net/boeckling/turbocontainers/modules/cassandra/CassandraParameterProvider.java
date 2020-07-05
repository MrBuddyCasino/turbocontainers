package net.boeckling.turbocontainers.modules.cassandra;

import com.datastax.driver.core.Cluster;
import net.boeckling.turbocontainers.parameter.ExecutionEnvironment;
import net.boeckling.turbocontainers.parameter.ParameterDescriptor;
import net.boeckling.turbocontainers.parameter.ParameterResolutionFailedException;
import net.boeckling.turbocontainers.parameter.SimpleParameterProvider;
import org.testcontainers.containers.CassandraContainer;

public class CassandraParameterProvider extends SimpleParameterProvider {

  protected CassandraParameterProvider() {
    super(Cluster.class, CassandraContainer.class);
  }

  @Override
  public Object resolveParameter(
    ParameterDescriptor param,
    ExecutionEnvironment env
  )
    throws ParameterResolutionFailedException {
    return tryCast(CassandraContainer.class, env.getContainer()).getCluster();
  }
}
