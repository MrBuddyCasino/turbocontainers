package net.boeckling.turbocontainers.api.init;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.boeckling.turbocontainers.api.init.internal.InitService;
import net.boeckling.turbocontainers.common.ServiceLoaderUtil;
import org.testcontainers.containers.GenericContainer;

public interface Init {
  static <C extends GenericContainer<?>, S> Builder<C, S> container(
    C container
  ) {
    return new Builder<>(container);
  }

  class Builder<C extends GenericContainer<?>, S> {
    private final C container;

    public Builder(C container) {
      this.container = container;
    }

    public C with(Consumer<C> initializer) {
      InitService initService = ServiceLoaderUtil.findService(
        InitService.class
      );
      BiConsumer<C, Object> initFn = (con, _ignored) -> initializer.accept(con);
      return initService.initialize(container, initFn);
    }

    public C with(BiConsumer<C, S> initializer) {
      InitService initService = ServiceLoaderUtil.findService(
        InitService.class
      );
      return initService.initialize(container, initializer);
    }
  }
}
