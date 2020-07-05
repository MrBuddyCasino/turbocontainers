package net.boeckling.turbocontainers.modules.cassandra;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.List;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.CassandraContainer;

@TurboContainers
public class CassandraStateManagerTest {
  @TurboContainer
  final CassandraContainer<?> container = CassandraConfiguration.CONTAINER;

  @Test
  @Order(1)
  void module_shouldInitializeContainer_onStartup(Cluster cluster) {
    Session session = cluster.connect("test1");
    ResultSet rs = session.execute("SELECT * from books");
    List<Row> all = rs.all();
    assertThat(all.size()).isEqualTo(1);
    assertThat(all.get(0).getString("title")).isEqualTo("Masters of Doom");

    session.execute(
      "INSERT INTO books (id, title) VALUES(2,'Founders At Work')"
    );
  }

  @Test
  @Order(2)
  void module_shouldResetState_betweenTests(Cluster cluster) {
    Session session = cluster.connect("test1");
    ResultSet rs = session.execute("SELECT * from books");
    List<Row> all = rs.all();
    assertThat(all.size()).isEqualTo(1);
    assertThat(all.get(0).getString("title")).isEqualTo("Masters of Doom");
  }
}
