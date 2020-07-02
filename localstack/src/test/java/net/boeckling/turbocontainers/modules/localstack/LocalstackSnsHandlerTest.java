package net.boeckling.turbocontainers.modules.localstack;

import static net.boeckling.turbocontainers.modules.localstack.LocalstackConfiguration.INIT_TOPIC_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.services.sns.AmazonSNS;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.sns.SnsClient;

@TurboContainers
public class LocalstackSnsHandlerTest {
  @TurboContainer
  final LocalStackContainer container = LocalstackConfiguration.CONTAINER;

  private static final String TEST_TOPIC = "test-topic";

  @Test
  @Order(1)
  void client_shouldCreateData_always(AmazonSNS sns) {
    int initialSize = sns.listTopics().getTopics().size();
    sns.createTopic(TEST_TOPIC);
    assertThat(sns.listTopics().getTopics().size()).isEqualTo(initialSize + 1);
  }

  @Test
  @Order(2)
  void module_shouldDeleteData_whenCreatedInPreviousTest(AmazonSNS sns) {
    assertThat(sns.listTopics().getTopics())
      .noneMatch(topic -> topic.getTopicArn().endsWith(TEST_TOPIC));
  }

  @Test
  @Order(3)
  void module_shouldPreserveInitialData_always(AmazonSNS sns) {
    assertThat(sns.listTopics().getTopics())
      .anyMatch(topic -> topic.getTopicArn().endsWith(INIT_TOPIC_NAME));
  }

  @Test
  @Order(4)
  void clientV2_shouldQueryDataSuccessfully_always(SnsClient sns) {
    assertThat(sns.listTopics().topics())
      .anyMatch(topic -> topic.topicArn().endsWith(INIT_TOPIC_NAME));
  }
}
