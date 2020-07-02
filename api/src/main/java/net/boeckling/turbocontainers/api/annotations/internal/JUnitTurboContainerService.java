package net.boeckling.turbocontainers.api.annotations.internal;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ParameterResolver;

public interface JUnitTurboContainerService
  extends BeforeEachCallback, ParameterResolver, ExecutionCondition {}
