package net.boeckling.turbocontainers.modules.localstack;

import static net.boeckling.turbocontainers.modules.localstack.LocalstackConfiguration.INIT_QUEUE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.sqs.SqsClient;

@TurboContainers
public class LocalstackSqsHandlerTest {
  public static final String PER_TEST_QUEUE = "test-queue";

  @TurboContainer
  final LocalStackContainer container = LocalstackConfiguration.CONTAINER;

  @Test
  @Order(1)
  void client_shouldCreateData_always(AmazonSQS sqs) {
    sqs.createQueue(PER_TEST_QUEUE);
    assertThat(sqs.getQueueUrl(PER_TEST_QUEUE).getQueueUrl()).isNotBlank();
  }

  @Test
  @Order(2)
  void module_shouldDeleteData_whenCreatedInPreviousTest(AmazonSQS sqs) {
    assertThatThrownBy(() -> sqs.getQueueUrl(PER_TEST_QUEUE).getQueueUrl())
      .isInstanceOf(QueueDoesNotExistException.class);
  }

  @Test
  @Order(3)
  void module_shouldRetainInitialData_always(AmazonSQS sqs) {
    assertThat(sqs.getQueueUrl(INIT_QUEUE_NAME).getQueueUrl()).isNotBlank();
  }

  @Test
  @Order(4)
  void clientv2_shouldQueryData_successfully(SqsClient sqs) {
    assertThat(sqs.getQueueUrl(b -> b.queueName(INIT_QUEUE_NAME)).queueUrl())
      .isNotBlank();
  }
}
