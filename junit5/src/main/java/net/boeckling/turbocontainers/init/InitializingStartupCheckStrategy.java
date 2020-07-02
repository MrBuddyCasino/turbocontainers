package net.boeckling.turbocontainers.init;

import static org.testcontainers.containers.startupcheck.StartupCheckStrategy.StartupStatus.SUCCESSFUL;

import com.github.dockerjava.api.DockerClient;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Accessor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.StartupCheckStrategy;

public class InitializingStartupCheckStrategy<C extends GenericContainer<?>, S>
  extends StartupCheckStrategy {
  private static final Logger LOGGER = LoggerFactory.getLogger(
    InitializingStartupCheckStrategy.class
  );

  private final C container;
  private final Runnable initializer;
  private final StartupCheckStrategy originalStrategy;
  private boolean hasInitialized = false;

  private InitializingStartupCheckStrategy(C container, Runnable r) {
    this.container = container;
    this.initializer = r;
    this.originalStrategy = container.getStartupCheckStrategy();
  }

  @Override
  public synchronized StartupStatus checkStartupState(
    DockerClient dockerClient,
    String containerId
  ) {
    StartupStatus startupStatus = originalStrategy.checkStartupState(
      dockerClient,
      containerId
    );

    if (startupStatus == SUCCESSFUL && (!hasInitialized)) {
      Accessor.getWaitStrategy(container).waitUntilReady(container);
      try {
        initializer.run();
        hasInitialized = true;
      } catch (RuntimeException e) {
        LOGGER.error(
          "Failed to initialize " + container.getDockerImageName(),
          e
        );
        return StartupStatus.FAILED;
      }
    }

    return startupStatus;
  }

  /**
   * No initializer: wrap with no-op.
   */
  public static <C extends GenericContainer<?>> void wrapStrategy(C container) {
    wrapStrategy(container, () -> {});
  }

  public static <C extends GenericContainer<?>, S> void wrapStrategy(
    C container,
    Runnable r
  ) {
    container.setStartupCheckStrategy(
      new InitializingStartupCheckStrategy<>(container, r)
    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InitializingStartupCheckStrategy<?, ?> that = (InitializingStartupCheckStrategy<?, ?>) o;
    return (
      hasInitialized == that.hasInitialized &&
      container.equals(that.container) &&
      initializer.equals(that.initializer) &&
      originalStrategy.equals(that.originalStrategy)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      container,
      initializer,
      originalStrategy,
      hasInitialized
    );
  }
}
