package net.boeckling.turbocontainers.init;

import java.util.List;
import java.util.function.Consumer;
import net.boeckling.turbocontainers.TurboContainerServiceImpl;
import net.boeckling.turbocontainers.api.annotations.internal.JUnitTurboContainerService;
import net.boeckling.turbocontainers.api.init.InitializerContext;
import net.boeckling.turbocontainers.api.init.internal.InitService;
import net.boeckling.turbocontainers.common.ServiceLoaderUtil;
import net.boeckling.turbocontainers.parameter.ParameterProvider;
import org.testcontainers.containers.GenericContainer;

public class InitServiceImpl implements InitService {
  private final List<ParameterProvider> paramProviders;

  public InitServiceImpl() {
    TurboContainerServiceImpl service = (TurboContainerServiceImpl) ServiceLoaderUtil.findService(
      JUnitTurboContainerService.class
    );
    this.paramProviders = service.getParamResolver().getParamProviders();
  }

  @Override
  public <C extends GenericContainer<?>> C initialize(
    C container,
    Consumer<InitializerContext<C>> initializer
  ) {
    Runnable r = () -> {
      // parameter resolution can only happen once the container is started
      initializer.accept(
        new InitializerContextImpl<>(container, paramProviders)
      );
    };
    InitializingStartupCheckStrategy.wrapStrategy(container, r);

    return container;
  }
}
