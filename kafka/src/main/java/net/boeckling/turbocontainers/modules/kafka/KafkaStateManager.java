package net.boeckling.turbocontainers.modules.kafka;

import static org.awaitility.Awaitility.await;
import static org.testcontainers.containers.KafkaContainer.ZOOKEEPER_PORT;

import java.time.Duration;
import java.util.Set;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient;
import kafka.zookeeper.ZooKeeperClient;
import net.boeckling.turbocontainers.state.InitAlwaysStateManager;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.apache.kafka.common.utils.Time;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.KafkaContainer;
import scala.collection.JavaConverters;

public class KafkaStateManager
  implements InitAlwaysStateManager<KafkaContainer> {

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof KafkaContainer;
  }

  @Override
  public void wipe(KafkaContainer container) {
    try (KafkaZkClient client = createKafkaZkClient(container)) {
      Set<String> topicsToDelete = JavaConverters.asJava(
        client.getAllTopicsInCluster()
      );

      for (String topic : topicsToDelete) {
        deleteTopic(topic, client);
      }
    }
  }

  private KafkaZkClient createKafkaZkClient(KafkaContainer kafkaContainer) {
    Integer zkPort = kafkaContainer.getMappedPort(ZOOKEEPER_PORT);
    String zkHost = kafkaContainer.getHost();

    ZooKeeperClient client = new ZooKeeperClient(
      zkHost + ":" + zkPort,
      Integer.MAX_VALUE,
      5000,
      10,
      Time.SYSTEM,
      "metricGroup",
      "metricType"
    );

    return new KafkaZkClient(client, false, Time.SYSTEM);
  }

  private void deleteTopic(String topic, KafkaZkClient zkClient) {
    AdminZkClient adminZkClient = new AdminZkClient(zkClient);

    // deletions take a while to become effective
    try {
      await()
        .atMost(Duration.ofSeconds(30))
        .pollInterval(Duration.ofMillis(100))
        .until(
          () -> {
            try {
              adminZkClient.deleteTopic(topic);
            } catch (UnknownTopicOrPartitionException ignored) {}

            return zkClient.topicExists(topic);
          }
        );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
