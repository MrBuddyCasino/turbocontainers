package net.boeckling.turbocontainers.api.init;

import org.testcontainers.containers.GenericContainer;

public interface InitializerContext<C extends GenericContainer> {
  /**
   * Current container instance.
   */
  C container();

  /**
   * A factory for clients, eg DataSource or MongoClient.
   */
  <S> S client(Class<S> clazz);

  /**
   * Run a database initialization script.
   */
  void initScript(String resource);
}
