package net.boeckling.turbocontainers.modules.localstack;

import static java.util.Collections.emptyList;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.boeckling.turbocontainers.events.LifecycleListener;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

public class SqsServiceHandler
  implements LifecycleListener<LocalStackContainer> {
  private AmazonSQS sqs;

  private static final Map<String, Collection<String>> QUEUES_TO_PRESERVE = new HashMap<>();

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof LocalStackContainer;
  }

  @Override
  public void afterContainerInitialized(LocalStackContainer container) {
    AmazonSQS sqs = getSqsClient(container);
    QUEUES_TO_PRESERVE.put(
      container.getContainerId(),
      sqs.listQueues().getQueueUrls()
    );
  }

  @Override
  public void beforeEachTest(LocalStackContainer container) {
    AmazonSQS sqs = getSqsClient(container);
    List<String> queuesToDelete = sqs.listQueues().getQueueUrls();
    Collection<String> queuesToPreserve = QUEUES_TO_PRESERVE.getOrDefault(
      container.getContainerId(),
      emptyList()
    );
    queuesToDelete.removeAll(queuesToPreserve);
    queuesToDelete.forEach(sqs::deleteQueue);
  }

  private AmazonSQS getSqsClient(LocalStackContainer container) {
    if (sqs == null) {
      sqs = createSqsClient(container);
    }
    return sqs;
  }

  static AmazonSQS createSqsClient(LocalStackContainer container) {
    return AmazonSQSClientBuilder
      .standard()
      .withEndpointConfiguration(container.getEndpointConfiguration(SQS))
      .withCredentials(container.getDefaultCredentialsProvider())
      .build();
  }

  static SqsClient createSqsClientV2(LocalStackContainer container) {
    return Common.configure(SqsClient.builder(), container, SQS).build();
  }
}
