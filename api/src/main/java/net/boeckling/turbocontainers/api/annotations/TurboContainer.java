package net.boeckling.turbocontainers.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code @Container} annotation is used in conjunction with the {@link TurboContainers} annotation
 * to mark containers that should be managed by the TurboContainers extension.
 *
 * @see TurboContainers
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface TurboContainer {
  /**
   * Disambiguate containers of the same type with a name
   * in order to decide which should be used for parameter
   * resolution.
   */
  String value() default "";
}
