package net.boeckling.turbocontainers.modules.kafka;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import net.boeckling.turbocontainers.state.InitAlwaysStateManager;
import org.apache.kafka.clients.admin.Admin;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.KafkaContainer;

public class KafkaStateManager
  implements InitAlwaysStateManager<KafkaContainer> {

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof KafkaContainer;
  }

  @Override
  public void wipe(KafkaContainer container) {
    Properties props = new Properties();
    props.put(BOOTSTRAP_SERVERS_CONFIG, container.getBootstrapServers());

    try (Admin admin = Admin.create(props)) {
      Set<String> topics = admin.listTopics().names().get();
      admin.deleteTopics(topics);
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
