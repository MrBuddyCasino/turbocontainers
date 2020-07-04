package net.boeckling.turbocontainers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.boeckling.turbocontainers.api.annotations.TurboContainer;
import net.boeckling.turbocontainers.api.annotations.TurboContainers;
import net.boeckling.turbocontainers.api.annotations.internal.JUnitTurboContainerService;
import net.boeckling.turbocontainers.events.BeforeEachTestEvent;
import net.boeckling.turbocontainers.events.EventPublisher;
import net.boeckling.turbocontainers.events.EventPublisherImpl;
import net.boeckling.turbocontainers.init.EventEmittingStartupCheckStrategy;
import net.boeckling.turbocontainers.init.InitializingStartupCheckStrategy;
import net.boeckling.turbocontainers.modules.ModuleRegistry;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.StartupCheckStrategy;
import org.testcontainers.lifecycle.Startable;

/**
 * Links the Testcontainer lifecycle to JUnit:
 * <ul>
 *   <li>start and initialize containers once before all tests</li>
 *   <li>reset container state between each test</li>
 * </ul>
 * <p>
 * This is meant to speed up tests by avoiding constant shutdown and restart of containers,
 * while still isolating tests between each other.
 * <p>
 * Use it by adding this to your test class, adding containers as needed:
 * <pre>
 *   &#64;RegisterExtension
 *   final JunitTestcontainerAdapter adapter = JunitTestcontainerAdapter
 *     .builder()
 *     .withContainer(Postgres.CONTAINER, Postgres::initialise)
 *     .build();
 * </pre>
 */
public class TurboContainerServiceImpl implements JUnitTurboContainerService {
  private final EventPublisher eventPublisher;

  private final JUnitParameterResolver paramResolver;

  public TurboContainerServiceImpl() {
    ModuleRegistry listeners = new ModuleRegistry();
    eventPublisher = new EventPublisherImpl(listeners);
    paramResolver =
      new JUnitParameterResolver(
        listeners.getParameterProviders(),
        this::findContainersWithDependencies
      );
  }

  @Override
  public void beforeEach(ExtensionContext ctx) {
    findContainers(ctx.getTestInstance())
      .stream()
      .peek(this::deepPatchStrategy)
      .peek(Startable::start)
      .map(BeforeEachTestEvent::new)
      .forEach(eventPublisher::publishEvent);
  }

  /**
   * If a container depends on other containers, make sure they emit events.
   */
  private void deepPatchStrategy(GenericContainer<?> container) {
    container
      .getDependencies()
      .stream()
      .filter(d -> d instanceof GenericContainer)
      .map(GenericContainer.class::cast)
      .forEach(this::deepPatchStrategy);

    StartupCheckStrategy strategy = container.getStartupCheckStrategy();
    if (strategy instanceof EventEmittingStartupCheckStrategy) {
      return;
    }

    if (!(strategy instanceof InitializingStartupCheckStrategy)) {
      // init is a no-op
      InitializingStartupCheckStrategy.wrapStrategy(container);
    }
    EventEmittingStartupCheckStrategy.wrapStrategy(container, eventPublisher);
  }

  private List<GenericContainer<?>> findContainers(
    Optional<Object> testInstance
  ) {
    return testInstance.map(this::findContainers).orElse(emptyList());
  }

  private Collection<GenericContainer<?>> findContainersWithDependencies(
    Optional<Object> testInstance
  ) {
    return findContainers(testInstance)
      .stream()
      .flatMap(c -> Stream.concat(Stream.of(c), c.getDependencies().stream()))
      .filter(c -> c instanceof GenericContainer)
      .map(c -> (GenericContainer<?>) c)
      .distinct()
      .collect(toList());
  }

  private List<GenericContainer<?>> findContainers(Object testInstance) {
    return ReflectionUtils
      .findFields(
        testInstance.getClass(),
        isContainer(),
        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN
      )
      .stream()
      .map(f -> getContainerInstance(testInstance, f))
      .collect(toList());
  }

  private static Predicate<Field> isContainer() {
    return field -> {
      boolean isAnnotatedWithContainer = AnnotationSupport.isAnnotated(
        field,
        TurboContainer.class
      );
      if (isAnnotatedWithContainer) {
        boolean isContainer = Container.class.isAssignableFrom(field.getType());

        if (!isContainer) {
          throw new ExtensionConfigurationException(
            String.format(
              "FieldName: %s does not implement Container",
              field.getName()
            )
          );
        }
        return true;
      }
      return false;
    };
  }

  private static GenericContainer<?> getContainerInstance(
    final Object testInstance,
    final Field field
  ) {
    try {
      field.setAccessible(true);
      return Preconditions.notNull(
        (GenericContainer<?>) field.get(testInstance),
        "Container " + field.getName() + " needs to be initialized"
      );
    } catch (IllegalAccessException e) {
      throw new ExtensionConfigurationException(
        "Can not access container defined in field " + field.getName()
      );
    }
  }

  @Override
  public boolean supportsParameter(
    ParameterContext param,
    ExtensionContext ext
  )
    throws ParameterResolutionException {
    return paramResolver.supportsParameter(param, ext);
  }

  @Override
  public Object resolveParameter(ParameterContext param, ExtensionContext ext)
    throws ParameterResolutionException {
    return paramResolver.resolveParameter(param, ext);
  }

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(
    ExtensionContext context
  ) {
    return findTurboContainers(context)
      .map(this::evaluate)
      .orElseThrow(
        () ->
          new ExtensionConfigurationException(
            "@" + TurboContainers.class.getSimpleName() + " not found"
          )
      );
  }

  private Optional<TurboContainers> findTurboContainers(
    ExtensionContext context
  ) {
    Optional<ExtensionContext> current = Optional.of(context);
    while (current.isPresent()) {
      Optional<TurboContainers> turboContainers = AnnotationUtils.findAnnotation(
        current.get().getRequiredTestClass(),
        TurboContainers.class
      );
      if (turboContainers.isPresent()) {
        return turboContainers;
      }
      current = current.get().getParent();
    }
    return Optional.empty();
  }

  private ConditionEvaluationResult evaluate(TurboContainers turboContainers) {
    if (turboContainers.disabledWithoutDocker()) {
      if (isDockerAvailable()) {
        return ConditionEvaluationResult.enabled("Docker is available");
      }
      return ConditionEvaluationResult.disabled(
        "disabledWithoutDocker is true and Docker is not available"
      );
    }
    return ConditionEvaluationResult.enabled("disabledWithoutDocker is false");
  }

  boolean isDockerAvailable() {
    try {
      DockerClientFactory.instance().client();
      return true;
    } catch (Throwable ex) {
      return false;
    }
  }

  public JUnitParameterResolver getParamResolver() {
    return paramResolver;
  }
}
