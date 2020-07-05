package net.boeckling.turbocontainers.modules.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.common.base.Splitter;
import java.util.List;
import net.boeckling.turbocontainers.modules.cli.Script;
import net.boeckling.turbocontainers.state.InitAlwaysStateManager;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.Container;

public class CassandraStateManager
  implements InitAlwaysStateManager<CassandraContainer<?>> {

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof CassandraContainer;
  }

  @Override
  public void wipe(CassandraContainer<?> container) {
    Cluster cluster = container.getCluster();
    Session session = cluster.connect();
    Script describeKeyspaces = CqlshScript.of(Script.of("DESCRIBE keyspaces;"));
    Container.ExecResult result = describeKeyspaces.runIn(container);
    List<String> toDelete = Splitter
      .on(" ")
      .omitEmptyStrings()
      .splitToList(result.getStdout());

    toDelete
      .stream()
      .filter(s -> !s.startsWith("system"))
      .forEach(ks -> session.execute("DROP KEYSPACE " + ks));
  }
}
