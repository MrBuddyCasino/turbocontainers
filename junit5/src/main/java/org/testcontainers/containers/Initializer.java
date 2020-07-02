package org.testcontainers.containers;

import java.util.function.BiConsumer;

public interface Initializer {
  <C extends Container<?>, S> C initialize(
    C container,
    BiConsumer<C, S> initializer
  );

  static <C extends Configuration.Container<?>, S> Initializer.Builder<C, S> init(
    C container
  ) {
    return new Initializer.Builder<>(container);
  }

  class Builder<C extends Configuration.Container<?>, S> {
    private final C container;

    public Builder(C container) {
      this.container = container;
    }

    public C with(BiConsumer<C, S> initializer) {
      Initializer initService = null;
      return container;
    }
  }
}
