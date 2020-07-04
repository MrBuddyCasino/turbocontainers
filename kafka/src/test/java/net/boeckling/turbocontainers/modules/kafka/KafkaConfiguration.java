package net.boeckling.turbocontainers.modules.kafka;

import java.util.function.BiConsumer;
import net.boeckling.turbocontainers.api.init.Init;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.testcontainers.containers.KafkaContainer;

public class KafkaConfiguration
  implements BiConsumer<KafkaContainer, KafkaConsumer<String, String>> {
  static final String INIT_TOPIC = "init_topic";
  static final KafkaContainer CONTAINER = Init
    .<KafkaContainer, KafkaConsumer<String, String>>container(
      new KafkaContainer()
        .withEmbeddedZookeeper()
        .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
        .withEnv("KAFKA_DELETE_TOPIC_ENABLE", "true")
        .withEnv("KAFKA_CONFLUENT_SUPPORT_METRICS_ENABLE", "false")
    )
    // triggers topic creation
    .with(new KafkaConfiguration());

  @Override
  public void accept(
    KafkaContainer container,
    KafkaConsumer<String, String> consumer
  ) {
    consumer.partitionsFor(INIT_TOPIC);
  }
}
