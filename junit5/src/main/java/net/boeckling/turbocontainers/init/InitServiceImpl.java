package net.boeckling.turbocontainers.init;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.BiConsumer;
import net.boeckling.turbocontainers.TurboContainerServiceImpl;
import net.boeckling.turbocontainers.api.annotations.internal.JUnitTurboContainerService;
import net.boeckling.turbocontainers.api.init.internal.InitService;
import net.boeckling.turbocontainers.common.ServiceLoaderUtil;
import net.boeckling.turbocontainers.parameter.*;
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
  public <C extends GenericContainer<?>, S> C initialize(
    C container,
    BiConsumer<C, S> initializer
  ) {
    Runnable r = () -> {
      // parameter resolution can only happen once the container is started
      S param = resolveParameter(container, initializer);
      initializer.accept(container, param);
    };
    InitializingStartupCheckStrategy.wrapStrategy(container, r);

    return container;
  }

  private <C extends GenericContainer<?>, S> S resolveParameter(
    C container,
    BiConsumer<C, S> initializer
  ) {
    // TODO: capture type parameters if available
    Class<?> secondArgType = getSecondArgType(initializer);
    if (secondArgType == null) {
      return null;
    }

    ParameterDescriptor desc = new ParameterDescriptorImpl(secondArgType);
    ExecutionEnvironment env = new ExecutionEnvironmentImpl(
      container,
      ExecutionEnvironment.Phase.INIT_CONTAINER
    );

    for (ParameterProvider provider : paramProviders) {
      if (provider.supportsParameter(desc)) {
        return (S) provider.resolveParameter(desc, env);
      }
    }
    return null;
  }

  private Class<?> getSecondArgType(BiConsumer<?, ?> initializer) {
    for (Type genericInterface : initializer
      .getClass()
      .getGenericInterfaces()) {
      // this genericInterface is the type-specific subclass of BiConsumer
      if (genericInterface instanceof Class) {
        for (Type anInterface : (
          (Class<?>) genericInterface
        ).getGenericInterfaces()) {
          // the actual BiConsumer
          if (anInterface instanceof ParameterizedType) {
            Type[] actualTypeArguments =
              ((ParameterizedType) anInterface).getActualTypeArguments();
            if (actualTypeArguments.length == 2) {
              Type type = actualTypeArguments[1];
              if (type instanceof Class) {
                return (Class<?>) type;
              }
              if (type instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) type).getRawType();
                if (rawType instanceof Class) {
                  return (Class<?>) rawType;
                }
              }
            }
          }
        }
      }
    }

    return null;
  }
}
