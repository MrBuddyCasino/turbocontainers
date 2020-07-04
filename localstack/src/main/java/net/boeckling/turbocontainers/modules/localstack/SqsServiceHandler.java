package net.boeckling.turbocontainers.modules.localstack;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import java.util.List;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.sqs.SqsClient;

public class SqsServiceHandler {
  private AmazonSQS sqs;

  public void wipe(LocalStackContainer container) {
    AmazonSQS sqs = getSqsClient(container);
    List<String> queuesToDelete = sqs.listQueues().getQueueUrls();
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
