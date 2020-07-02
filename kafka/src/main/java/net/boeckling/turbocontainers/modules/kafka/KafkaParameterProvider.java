package net.boeckling.turbocontainers.modules.kafka;

import java.util.Properties;
import net.boeckling.turbocontainers.parameter.ExecutionEnvironment;
import net.boeckling.turbocontainers.parameter.ParameterDescriptor;
import net.boeckling.turbocontainers.parameter.SimpleParamProvider;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.testcontainers.containers.KafkaContainer;

public class KafkaParameterProvider extends SimpleParamProvider {
  private KafkaConsumer<String, String> consumer;

  public KafkaParameterProvider() {
    super(KafkaConsumer.class, KafkaContainer.class);
  }

  @Override
  public Object resolveParameter(
    ParameterDescriptor param,
    ExecutionEnvironment env
  ) {
    KafkaContainer kafkaContainer = tryCast(
      KafkaContainer.class,
      env.getContainer()
    );
    if (consumer == null) {
      consumer = createConsumer(kafkaContainer.getBootstrapServers());
    }
    return consumer;
  }

  private KafkaConsumer<String, String> createConsumer(
    String bootstrapServers
  ) {
    Properties props = new Properties();
    props.put("bootstrap.servers", bootstrapServers);
    props.put("group.id", "test-container-consumer-group");
    props.put("key.deserializer", StringDeserializer.class.getName());
    props.put("value.deserializer", StringDeserializer.class.getName());

    return new KafkaConsumer<>(props);
  }
}
