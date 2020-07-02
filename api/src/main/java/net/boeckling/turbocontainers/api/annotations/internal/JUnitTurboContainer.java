package net.boeckling.turbocontainers.api.annotations.internal;

import net.boeckling.turbocontainers.common.ServiceLoaderUtil;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

public class JUnitTurboContainer implements JUnitTurboContainerService {
  private final JUnitTurboContainerService delegate;

  public JUnitTurboContainer() {
    delegate = ServiceLoaderUtil.findService(JUnitTurboContainerService.class);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    delegate.beforeEach(context);
  }

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(
    ExtensionContext context
  ) {
    return delegate.evaluateExecutionCondition(context);
  }

  @Override
  public boolean supportsParameter(
    ParameterContext parameterContext,
    ExtensionContext extensionContext
  )
    throws ParameterResolutionException {
    return delegate.supportsParameter(parameterContext, extensionContext);
  }

  @Override
  public Object resolveParameter(
    ParameterContext parameterContext,
    ExtensionContext extensionContext
  )
    throws ParameterResolutionException {
    return delegate.resolveParameter(parameterContext, extensionContext);
  }
}
