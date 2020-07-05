package net.boeckling.turbocontainers.modules.localstack;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import net.boeckling.turbocontainers.parameter.ExecutionEnvironment;
import net.boeckling.turbocontainers.parameter.ParameterDescriptor;
import net.boeckling.turbocontainers.parameter.ParameterProvider;
import net.boeckling.turbocontainers.parameter.ParameterResolutionFailedException;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

public class LocalstackParameterProvider implements ParameterProvider {

  @Override
  public boolean supportsParameter(ParameterDescriptor param) {
    return (
      param.getType().isAssignableFrom(AmazonS3.class) ||
      param.getType().isAssignableFrom(S3Client.class) ||
      param.getType().isAssignableFrom(AmazonSQS.class) ||
      param.getType().isAssignableFrom(SqsClient.class) ||
      param.getType().isAssignableFrom(AmazonSNS.class) ||
      param.getType().isAssignableFrom(SnsClient.class)
    );
  }

  @Override
  public Object resolveParameter(
    ParameterDescriptor param,
    ExecutionEnvironment env
  ) {
    LocalStackContainer container = tryCast(
      LocalStackContainer.class,
      env.getContainer()
    );

    if (param.getType().isAssignableFrom(AmazonS3.class)) {
      return S3ServiceHandler.createS3Client(container);
    }
    if (param.getType().isAssignableFrom(S3Client.class)) {
      return S3ServiceHandler.createS3ClientV2(container);
    }

    if (param.getType().isAssignableFrom(AmazonSQS.class)) {
      return SqsServiceHandler.createSqsClient(container);
    }
    if (param.getType().isAssignableFrom(SqsClient.class)) {
      return SqsServiceHandler.createSqsClientV2(container);
    }

    if (param.getType().isAssignableFrom(AmazonSNS.class)) {
      return SnsServiceHandler.createSnsClient(container);
    }
    if (param.getType().isAssignableFrom(SnsClient.class)) {
      return SnsServiceHandler.createSnsClientV2(container);
    }

    throw new ParameterResolutionFailedException(
      "not supported: " + param.getType()
    );
  }
}
