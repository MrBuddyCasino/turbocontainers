package org.testcontainers.containers;

import java.util.function.BiConsumer;
import javax.sql.DataSource;

public class Configuration {

  interface Container<SELF extends Container<SELF>> {}

  static class GenericContainer<SELF extends GenericContainer<SELF>>
    implements Container<SELF> {}

  // works
  public static <SELF extends GenericContainer<SELF>> void initialise(
    GenericContainer<SELF> container,
    DataSource ds
  ) {}

  public static final GenericContainer<?> CONTAINER_1 = Initializer
    .<GenericContainer, DataSource>init(new GenericContainer<>())
    .with(Configuration::initialise);

  // doesn't work
  /*
  static <SELF extends GenericContainer<SELF>> BiConsumer<GenericContainer<SELF>, DataSource> f = (con, ds) -> {};
  public static final GenericContainer<?> CONTAINER_2 = new Initializer.Builder<>(
    new GenericContainer<>()
  )
  .with(f);
  */

  public static void main(String[] args) {
    CONTAINER_1.getClass();
  }
}
