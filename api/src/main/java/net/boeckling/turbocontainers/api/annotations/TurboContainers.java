package net.boeckling.turbocontainers.api.annotations;

import java.lang.annotation.*;
import net.boeckling.turbocontainers.api.annotations.internal.JUnitTurboContainer;
import org.junit.jupiter.api.extension.ExtendWith;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(JUnitTurboContainer.class)
@Inherited
public @interface TurboContainers {
  /**
   * Whether tests should be disabled (rather than failing) when Docker is not available.
   */
  boolean disabledWithoutDocker() default false;
}
