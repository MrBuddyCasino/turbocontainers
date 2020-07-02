package net.boeckling.turbocontainers.modules.localstack;

import static java.util.stream.Collectors.toList;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import net.boeckling.turbocontainers.events.LifecycleListener;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;

public class LocalstackLifecycleListener
  implements LifecycleListener<LocalStackContainer> {
  private final Set<Service> supportedServices = EnumSet.of(SQS, SNS, S3);

  private final S3ServiceHandler s3Handler = new S3ServiceHandler();
  private final SnsServiceHandler snsHandler = new SnsServiceHandler();
  private final SqsServiceHandler sqsHandler = new SqsServiceHandler();

  @Override
  public boolean supportsContainer(Container<?> container) {
    if (!(container instanceof LocalStackContainer)) {
      return false;
    }
    LocalStackContainer lsc = (LocalStackContainer) container;

    List<Service> services = getServices(lsc);
    return supportedServices.containsAll(services);
  }

  @Override
  public void afterContainerInitialized(LocalStackContainer container) {
    for (Service service : getServices(container)) {
      switch (service) {
        case S3:
          s3Handler.afterContainerInitialized(container);
          break;
        case SNS:
          snsHandler.afterContainerInitialized(container);
          break;
        case SQS:
          sqsHandler.afterContainerInitialized(container);
          break;
        default:
          throw new UnsupportedOperationException(
            "Unsupported service: " + service.getLocalStackName()
          );
      }
    }
  }

  @Override
  public void beforeEachTest(LocalStackContainer container) {
    for (Service service : getServices(container)) {
      switch (service) {
        case S3:
          s3Handler.beforeEachTest(container);
          break;
        case SNS:
          snsHandler.beforeEachTest(container);
          break;
        case SQS:
          sqsHandler.beforeEachTest(container);
          break;
        default:
          throw new UnsupportedOperationException(
            "Unsupported service: " + service.getLocalStackName()
          );
      }
    }
  }

  private List<Service> getServices(LocalStackContainer container) {
    return Stream
      .of(container.getEnvMap().get("SERVICES").split(","))
      .filter(str -> !str.isEmpty())
      .flatMap(
        str ->
          Stream
            .of(Service.values())
            .filter(svc -> svc.getLocalStackName().equalsIgnoreCase(str))
      )
      .collect(toList());
  }
}
