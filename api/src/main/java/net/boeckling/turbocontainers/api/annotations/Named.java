package net.boeckling.turbocontainers.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Disambiguate containers of the same type with a name
 * in order to decide which should be used for parameter
 * resolution.
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Named {
  String value() default "";
}
