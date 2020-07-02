package net.boeckling.turbocontainers.api.init.internal;

import java.util.function.BiConsumer;
import org.testcontainers.containers.GenericContainer;

public interface InitService {
  <C extends GenericContainer<?>, S> C initialize(
    C container,
    BiConsumer<C, S> initializer
  );
}
