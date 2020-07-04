package net.boeckling.turbocontainers.init;

import static org.testcontainers.containers.startupcheck.StartupCheckStrategy.StartupStatus.SUCCESSFUL;

import com.github.dockerjava.api.DockerClient;
import net.boeckling.turbocontainers.events.AfterContainerInitializedEvent;
import net.boeckling.turbocontainers.events.EventPublisher;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.StartupCheckStrategy;

public class EventEmittingStartupCheckStrategy extends StartupCheckStrategy {
  private final StartupCheckStrategy delegate;
  private final GenericContainer<?> container;
  private final EventPublisher eventPublisher;
  private boolean hasInitialized = false;

  public EventEmittingStartupCheckStrategy(
    GenericContainer<?> container,
    EventPublisher eventPublisher
  ) {
    this.delegate = container.getStartupCheckStrategy();
    this.container = container;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public StartupStatus checkStartupState(
    DockerClient dockerClient,
    String containerId
  ) {
    StartupStatus startupStatus = delegate.checkStartupState(
      dockerClient,
      containerId
    );
    if (startupStatus == SUCCESSFUL && (!hasInitialized)) {
      hasInitialized = true;
      eventPublisher.publishEvent(
        new AfterContainerInitializedEvent<>(container)
      );
    }
    return startupStatus;
  }

  public StartupCheckStrategy getDelegate() {
    return delegate;
  }

  public static void wrapStrategy(
    GenericContainer<?> container,
    EventPublisher eventPublisher
  ) {
    container.setStartupCheckStrategy(
      new EventEmittingStartupCheckStrategy(container, eventPublisher)
    );
  }
}
