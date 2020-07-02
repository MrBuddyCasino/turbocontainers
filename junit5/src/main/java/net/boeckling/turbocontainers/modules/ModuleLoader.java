package net.boeckling.turbocontainers.modules;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ModuleLoader {

  public List<Module> findModules() {
    return findModules(null);
  }

  /**
   * Method for locating available methods, using JDK {@link ServiceLoader}
   * facility, along with module-provided SPI.
   *<p>
   * Note that method does not do any caching, so calls should be considered
   * potentially expensive.
   */
  public static List<Module> findModules(ClassLoader classLoader) {
    List<Module> modules = new ArrayList<>();
    ServiceLoader<Module> loader = secureGetServiceLoader(
      Module.class,
      classLoader
    );
    for (Module module : loader) {
      modules.add(module);
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
