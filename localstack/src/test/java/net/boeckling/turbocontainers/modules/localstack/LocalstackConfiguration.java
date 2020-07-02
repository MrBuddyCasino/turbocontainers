package net.boeckling.turbocontainers.modules.localstack;

import static net.boeckling.turbocontainers.api.init.Init.container;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.localstack.LocalStackContainer;

public class LocalstackConfiguration {
  public static final String INIT_BUCKET_NAME = "init-bucket";
  public static final String INIT_TOPIC_NAME = "init-topic";
  public static final String INIT_QUEUE_NAME = "init-queue";

  static final LocalStackContainer CONTAINER = container(
      new LocalStackContainer("0.11.3")
        .withNetwork(Network.SHARED)
        .withNetworkAliases("localstack")
        .withEnv("HOSTNAME_EXTERNAL", "localhost")
        .withServices(S3, SQS, SNS)
        .withEnv("DEBUG", "1")
    )
    .with(LocalstackConfiguration::initialize);

  static void initialize(LocalStackContainer container) {
    // S3
    AmazonS3ClientBuilder
      .standard()
      .withEndpointConfiguration(container.getEndpointConfiguration(S3))
      .withCredentials(container.getDefaultCredentialsProvider())
      .build()
      .createBucket(INIT_BUCKET_NAME);

    // SQS
    AmazonSQS sqs = AmazonSQSClientBuilder
      .standard()
      .withEndpointConfiguration(container.getEndpointConfiguration(SQS))
      .withCredentials(container.getDefaultCredentialsProvider())
      .build();
    sqs.createQueue(INIT_QUEUE_NAME);

    // SNS
    AmazonSNS sns = AmazonSNSClientBuilder
      .standard()
      .withEndpointConfiguration(container.getEndpointConfiguration(SNS))
      .withCredentials(container.getDefaultCredentialsProvider())
      .build();
    sns.createTopic(INIT_TOPIC_NAME);
  }
}
