package net.boeckling.turbocontainers.api.init;

import java.util.function.Consumer;
import net.boeckling.turbocontainers.api.init.internal.InitService;
import net.boeckling.turbocontainers.common.ServiceLoaderUtil;
import org.testcontainers.containers.GenericContainer;

/**
 * Convenience helper to initialise a container with some data before test are run.
 */
public interface Init {
  static <C extends GenericContainer> Builder<C> container(C container) {
    return new Builder<>(container);
  }

  class Builder<C extends GenericContainer> {
    private final C container;

    public Builder(C container) {
      this.container = container;
    }

    public C with(Consumer<InitializerContext<C>> initializer) {
      InitService initService = ServiceLoaderUtil.findService(
        InitService.class
      );
      return initService.initialize(container, initializer);
    }
  }
}
