package net.boeckling.turbocontainers;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.modules.ModuleRegistry;
import net.boeckling.turbocontainers.modules.RegisteredModule;
import net.boeckling.turbocontainers.parameter.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.util.AnnotationUtils;
import org.testcontainers.containers.GenericContainer;

public class JUnitParameterResolver implements ParameterResolver {
  private final List<ParameterProvider> paramProviders;
  private final Function<Optional<Object>, Collection<GenericContainer<?>>> containerProvider;
  private final ModuleRegistry moduleRegistry;

  public JUnitParameterResolver(
    Function<Optional<Object>, Collection<GenericContainer<?>>> containerProvider,
    ModuleRegistry moduleRegistry
  ) {
    this.containerProvider = containerProvider;
    this.moduleRegistry = moduleRegistry;

    paramProviders =
      moduleRegistry
        .getAll()
        .stream()
        .map(RegisteredModule::getParameterProviders)
        .flatMap(Collection::stream)
        .collect(toList());
  }

  @Override
  public boolean supportsParameter(
    ParameterContext param,
    ExtensionContext ext
  )
    throws ParameterResolutionException {
    ParameterDescriptor desc = new ParameterDescriptorImpl(
      param.getParameter().getType()
    );
    return paramProviders
      .stream()
      .anyMatch(prov -> prov.supportsParameter(desc));
  }

  @Override
  public Object resolveParameter(
    ParameterContext paramContext,
    ExtensionContext ext
  )
    throws ParameterResolutionException {
    ParameterDescriptor paramDesc = new ParameterDescriptorImpl(
      paramContext.getParameter().getType()
    );

    RegisteredModule module = moduleRegistry
      .findModuleForParameterType(paramDesc)
      .orElseThrow(
        () ->
          new ParameterResolutionException(
            "No module found from which to construct a " +
            paramContext.getParameter().getType().getName()
          )
      );

    //noinspection OptionalGetWithoutIsPresent
    ParameterProvider paramProvider = module
      .getParameterProviders()
      .stream()
      .filter(prov -> prov.supportsParameter(paramDesc))
      .findFirst()
      .get();

    List<GenericContainer<?>> containers = containerProvider
      .apply(ext.getTestInstance())
      .stream()
      .filter(module::supportsContainer)
      .collect(toList());

    if (containers.isEmpty()) {
      throw new ParameterResolutionException(
        "no Container found from which to construct a " +
        paramContext.getParameter().getType().getName()
      );
    }

    if (containers.size() == 1) {
      ExecutionEnvironment env = new ExecutionEnvironmentImpl(
        containers.get(0),
        ExecutionEnvironment.Phase.RUN_TEST
      );
      try {
        return paramProvider.resolveParameter(paramDesc, env);
      } catch (ParameterResolutionFailedException e) {
        throw new ParameterResolutionException(
          "Parameter resolution failed",
          e
        );
      }
    }

    TurboContainer named = paramContext
      .findAnnotation(TurboContainer.class)
      .orElseThrow(
        () ->
          new ParameterResolutionException(
            "Ambiguous parameter in " +
            paramContext.getDeclaringExecutable() +
            ", found " +
            containers.size() +
            " potential container matches. Assign a container name to disambiguate."
          )
      );

    List<Field> annotatedFields = AnnotationUtils.findAnnotatedFields(
      ext.getRequiredTestClass(),
      TurboContainer.class,
      f -> f.getAnnotation(TurboContainer.class).value().equals(named.value())
    );

    if (annotatedFields.isEmpty()) {
      throw new ParameterResolutionException(
        "Found " +
        TurboContainer.class.getName() +
        " annotation on parameter " +
        paramContext.getDeclaringExecutable() +
        " with value " +
        named.value() +
        ", but no matching container field."
      );
    }

    if (annotatedFields.size() == 1) {
      ExecutionEnvironment env = new ExecutionEnvironmentImpl(
        containers.get(0),
        ExecutionEnvironment.Phase.RUN_TEST
      );

      try {
        return paramProvider.resolveParameter(paramDesc, env);
      } catch (ParameterResolutionFailedException e) {
        throw new ParameterResolutionException(
          "Parameter resolution failed",
          e
        );
      }
    }

    throw new ParameterResolutionException(
      "Found " +
      TurboContainer.class.getName() +
      " annotation on parameter " +
      paramContext.getDeclaringExecutable() +
      " with value " +
      named.value() +
      ", but with " +
      annotatedFields.size() +
      " matching container fields."
    );
  }

  public List<ParameterProvider> getParamProviders() {
    return paramProviders;
  }
}
