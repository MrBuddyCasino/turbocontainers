package net.boeckling.turbocontainers.api.init.internal;

import java.util.function.Consumer;
import net.boeckling.turbocontainers.api.init.InitializerContext;
import org.testcontainers.containers.GenericContainer;

public interface InitService {
  <C extends GenericContainer<?>> C initialize(
    C container,
    Consumer<InitializerContext<C>> accept
  );
}
