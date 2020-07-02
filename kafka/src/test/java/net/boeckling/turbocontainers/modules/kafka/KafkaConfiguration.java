package net.boeckling.turbocontainers.modules.kafka;

import net.boeckling.turbocontainers.api.init.Init;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.testcontainers.containers.KafkaContainer;

public class KafkaConfiguration {
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
    .with((_ignored, consumer) -> consumer.partitionsFor(INIT_TOPIC));
}
