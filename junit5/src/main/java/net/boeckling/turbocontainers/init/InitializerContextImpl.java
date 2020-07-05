package net.boeckling.turbocontainers.init;

import java.util.List;
import java.util.Optional;
import net.boeckling.turbocontainers.api.init.InitializerContext;
import net.boeckling.turbocontainers.parameter.*;
import net.boeckling.turbocontainers.script.ScriptRunner;
import org.testcontainers.containers.GenericContainer;

public class InitializerContextImpl<C extends GenericContainer<?>>
  implements InitializerContext<C> {
  private final C container;
  private final List<ParameterProvider> paramProviders;
  private final Optional<ScriptRunner> scriptRunner;

  public InitializerContextImpl(
    C container,
    List<ParameterProvider> paramProviders,
    Optional<ScriptRunner> scriptRunner
  ) {
    this.container = container;
    this.paramProviders = paramProviders;
    this.scriptRunner = scriptRunner;
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

  @Override
  public void initScript(String resource) {
    if (!scriptRunner.isPresent()) {
      throw new UnsupportedOperationException(
        "Can't execute script " + resource + ", no registered runner found"
      );
    }
    scriptRunner.get().runScript(container, resource);
  }
}
