package net.boeckling.turbocontainers.init;

import java.util.List;
import net.boeckling.turbocontainers.api.init.InitializerContext;
import net.boeckling.turbocontainers.parameter.*;
import org.testcontainers.containers.GenericContainer;

public class InitializerContextImpl<C extends GenericContainer<?>>
  implements InitializerContext<C> {
  private final C container;
  private final List<ParameterProvider> paramProviders;

  public InitializerContextImpl(
    C container,
    List<ParameterProvider> paramProviders
  ) {
    this.container = container;
    this.paramProviders = paramProviders;
  }

  @Override
  public C container() {
    return container;
  }

  @Override
  public <S> S client(Class<S> clazz) {
    ParameterDescriptor desc = new ParameterDescriptorImpl(clazz);
    ExecutionEnvironment env = new ExecutionEnvironmentImpl(
      container,
      ExecutionEnvironment.Phase.INIT_CONTAINER
    );

    for (ParameterProvider provider : paramProviders) {
      if (provider.supportsParameter(desc)) {
        @SuppressWarnings("unchecked")
        S param = (S) provider.resolveParameter(desc, env);
        return param;
      }
    }
    throw new IllegalArgumentException(
      "parameter type unsupported: " + clazz.getName()
    );
  }
}
