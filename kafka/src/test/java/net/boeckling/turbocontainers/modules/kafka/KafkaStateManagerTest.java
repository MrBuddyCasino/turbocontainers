package net.boeckling.turbocontainers.modules.kafka;

import static net.boeckling.turbocontainers.modules.kafka.KafkaConfiguration.INIT_TOPIC;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;

@TurboContainers
public class KafkaStateManagerTest {
  public static final String PER_TEST_TOPIC = "topic";

  @TurboContainer
  final KafkaContainer kafka = KafkaConfiguration.CONTAINER;

  @Test
  @Order(1)
  void kafka_shouldCreateTopic_whenTriggered() {
    KafkaConsumer<String, String> consumer = createConsumer(
      KafkaConfiguration.CONTAINER.getBootstrapServers()
    );

    consumer.partitionsFor(PER_TEST_TOPIC); // triggers topic creation
    assertThat(consumer.listTopics().keySet()).contains(PER_TEST_TOPIC);
  }

  @Test
  @Order(2)
  void kafka_shouldDiscardTopicsFromPreviousTest_whenTopicsWereCreated() {
    KafkaConsumer<String, String> consumer = createConsumer(
      KafkaConfiguration.CONTAINER.getBootstrapServers()
    );

    assertThat(consumer.listTopics().keySet()).doesNotContain(PER_TEST_TOPIC);
  }

  @Test
  @Order(3)
  void kafka_shouldPreserveInitData_always() {
    KafkaConsumer<String, String> consumer = createConsumer(
      KafkaConfiguration.CONTAINER.getBootstrapServers()
    );

    assertThat(consumer.listTopics().keySet()).contains(INIT_TOPIC);
  }

  static KafkaConsumer<String, String> createConsumer(String bootstrapServers) {
    Properties props = new Properties();
    props.put("bootstrap.servers", bootstrapServers);
    props.put("group.id", "test-container-consumer-group");
    props.put("key.deserializer", StringDeserializer.class.getName());
    props.put("value.deserializer", StringDeserializer.class.getName());

    return new KafkaConsumer<>(props);
  }
}
