package net.boeckling.turbocontainers.modules.localstack;

import static java.util.stream.Collectors.toList;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.boeckling.turbocontainers.events.LifecycleListener;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.services.sns.SnsClient;

public class SnsServiceHandler
  implements LifecycleListener<LocalStackContainer> {
  private static final Map<String, SnsState> CONTAINER_STATE = new HashMap<>();

  private AmazonSNS sns;

  static class SnsState {
    private final List<String> endpointsToPreserve = new ArrayList<>();
    private final List<String> appsToPreserve = new ArrayList<>();
    private final List<String> topicsToPreserve = new ArrayList<>();
  }

  @Override
  public boolean supportsContainer(Container<?> container) {
    return container instanceof LocalStackContainer;
  }

  @Override
  public void afterContainerInitialized(LocalStackContainer container) {
    SnsState snsState = new SnsState();
    snsState.topicsToPreserve.addAll(getTopicArns(container));
    snsState.appsToPreserve.addAll(getPlatformAppArns(container));

    for (String app : snsState.appsToPreserve) {
      List<String> endpoints = getEndpointArns(container, app);
      snsState.endpointsToPreserve.addAll(endpoints);
    }
    CONTAINER_STATE.put(container.getContainerId(), snsState);
  }

  @Override
  public void beforeEachTest(LocalStackContainer container) {
    AmazonSNS sns = getSns(container);
    SnsState snsState = CONTAINER_STATE.getOrDefault(
      container.getContainerId(),
      new SnsState()
    );

    List<String> topicsToDelete = getTopicArns(container);
    topicsToDelete.removeAll(snsState.topicsToPreserve);
    topicsToDelete.forEach(sns::deleteTopic);

    List<String> appsToDelete = getPlatformAppArns(container);
    appsToDelete.removeAll(snsState.appsToPreserve);
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
    endpointsToDelete.removeAll(snsState.endpointsToPreserve);

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
