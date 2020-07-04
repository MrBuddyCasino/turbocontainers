package net.boeckling.turbocontainers.modules.localstack;

import static java.util.stream.Collectors.toList;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;
import java.util.ArrayList;
import java.util.List;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.sns.SnsClient;

public class SnsServiceHandler {
  private AmazonSNS sns;

  public void wipe(LocalStackContainer container) {
    AmazonSNS sns = getSns(container);

    List<String> topicsToDelete = getTopicArns(container);
    topicsToDelete.forEach(sns::deleteTopic);

    List<String> appsToDelete = getPlatformAppArns(container);
    for (String app : appsToDelete) {
      DeletePlatformApplicationRequest request = new DeletePlatformApplicationRequest()
      .withPlatformApplicationArn(app);
      sns.deletePlatformApplication(request);
    }

    List<String> apps = getPlatformAppArns(container);
    List<String> endpointsToDelete = new ArrayList<>();
    for (String app : apps) {
      endpointsToDelete.addAll(getEndpointArns(container, app));
    }

    for (String endpoint : endpointsToDelete) {
      DeleteEndpointRequest request = new DeleteEndpointRequest()
      .withEndpointArn(endpoint);
      sns.deleteEndpoint(request);
    }
  }

  private List<String> getEndpointArns(
    LocalStackContainer container,
    String app
  ) {
    ListEndpointsByPlatformApplicationRequest request = new ListEndpointsByPlatformApplicationRequest()
    .withPlatformApplicationArn(app);
    ListEndpointsByPlatformApplicationResult result = getSns(container)
      .listEndpointsByPlatformApplication(request);
    List<Endpoint> endpoints = result.getEndpoints();

    while (result.getNextToken() != null) {
      request =
        new ListEndpointsByPlatformApplicationRequest()
          .withPlatformApplicationArn(app)
          .withNextToken(result.getNextToken());
      result = getSns(container).listEndpointsByPlatformApplication(request);
      endpoints.addAll(result.getEndpoints());
    }

    return endpoints.stream().map(Endpoint::getEndpointArn).collect(toList());
  }

  private List<String> getTopicArns(LocalStackContainer container) {
    ListTopicsResult result = getSns(container).listTopics();
    List<Topic> topics = result.getTopics();

    while (result.getNextToken() != null) {
      ListTopicsRequest request = new ListTopicsRequest()
      .withNextToken(result.getNextToken());
      result = getSns(container).listTopics(request);
      topics.addAll(result.getTopics());
    }

    return topics.stream().map(Topic::getTopicArn).collect(toList());
  }

  private List<String> getPlatformAppArns(LocalStackContainer container) {
    ListPlatformApplicationsResult result = getSns(container)
      .listPlatformApplications();
    List<PlatformApplication> apps = result.getPlatformApplications();

    while (result.getNextToken() != null) {
      ListPlatformApplicationsRequest request = new ListPlatformApplicationsRequest()
      .withNextToken(result.getNextToken());
      result = getSns(container).listPlatformApplications(request);
      apps.addAll(result.getPlatformApplications());
    }

    return apps
      .stream()
      .map(PlatformApplication::getPlatformApplicationArn)
      .collect(toList());
  }

  private AmazonSNS getSns(LocalStackContainer container) {
    if (sns == null) {
      sns = createSnsClient(container);
    }
    return sns;
  }

  static AmazonSNS createSnsClient(LocalStackContainer container) {
    return AmazonSNSClientBuilder
      .standard()
      .withEndpointConfiguration(container.getEndpointConfiguration(SNS))
      .withCredentials(container.getDefaultCredentialsProvider())
      .build();
  }

  static SnsClient createSnsClientV2(LocalStackContainer container) {
    return Common.configure(SnsClient.builder(), container, SNS).build();
  }
}
