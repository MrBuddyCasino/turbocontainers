package net.boeckling.turbocontainers.api.init;

import org.testcontainers.containers.GenericContainer;

public interface InitializerContext<C extends GenericContainer<?>> {
  C container();
  <S> S client(Class<S> clazz);
}
