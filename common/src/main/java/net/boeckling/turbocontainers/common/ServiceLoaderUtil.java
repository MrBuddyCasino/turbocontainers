package net.boeckling.turbocontainers.common;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ServiceLoaderUtil {

  public static <T> T findService(Class<T> clazz) {
    List<T> services = findServices(clazz);
    if (services.isEmpty()) {
      throw new RuntimeException(
        "No implementation for " + clazz.getName() + " found on the classpath."
      );
    }

    if (services.size() == 1) {
      return services.get(0);
    }

    throw new RuntimeException(
      "Found more than one implementation of " +
      clazz.getName() +
      " on the classpath."
    );
  }

  public static <T> List<T> findServices(Class<T> clazz) {
    return findServices(clazz, null);
  }

  /**
   * Method for locating available methods, using JDK {@link ServiceLoader}
   * facility, along with module-provided SPI.
   *<p>
   * Note that method does not do any caching, so calls should be considered
   * potentially expensive.
   */
  public static <T> List<T> findServices(
    Class<T> clazz,
    ClassLoader classLoader
  ) {
    List<T> modules = new ArrayList<>();
    ServiceLoader<T> loader = secureGetServiceLoader(clazz, classLoader);
    for (T service : loader) {
      modules.add(service);
    }
    return modules;
  }

  private static <T> ServiceLoader<T> secureGetServiceLoader(
    final Class<T> clazz,
    final ClassLoader classLoader
  ) {
    final SecurityManager sm = System.getSecurityManager();
    if (sm == null) {
      return (classLoader == null)
        ? ServiceLoader.load(clazz)
        : ServiceLoader.load(clazz, classLoader);
    }
    return AccessController.doPrivileged(
      (PrivilegedAction<ServiceLoader<T>>) () ->
        (classLoader == null)
          ? ServiceLoader.load(clazz)
          : ServiceLoader.load(clazz, classLoader)
    );
  }
}
