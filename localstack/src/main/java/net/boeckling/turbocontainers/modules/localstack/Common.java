package net.boeckling.turbocontainers.modules.localstack;

import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.regions.Region;

public class Common {

  static <BuilderT extends AwsClientBuilder<BuilderT, ClientT>, ClientT> BuilderT configure(
    AwsClientBuilder<BuilderT, ClientT> builder,
    LocalStackContainer container,
    LocalStackContainer.Service service
  ) {
    return builder
      .endpointOverride(container.getEndpointOverride(service))
      .credentialsProvider(
        StaticCredentialsProvider.create(
          AwsBasicCredentials.create(
            container.getAccessKey(),
            container.getSecretKey()
          )
        )
      )
      .region(Region.of(container.getRegion()));
  }
}
