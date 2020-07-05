package net.boeckling.turbocontainers.script;

import static net.boeckling.turbocontainers.log.ContainerLogger.logger;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.script.ScriptException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

public abstract class AbstractScriptRunner implements ScriptRunner {

  @Override
  public void runScript(GenericContainer<?> container, String initScriptPath) {
    try {
      URL resource = Thread
        .currentThread()
        .getContextClassLoader()
        .getResource(initScriptPath);
      if (resource == null) {
        logger(container)
          .warn("Could not load classpath init script: {}", initScriptPath);
        throw new ScriptUtils.ScriptLoadException(
          "Could not load classpath init script: " +
          initScriptPath +
          ". Resource not found."
        );
      }
      String initScript = IOUtils.toString(resource, StandardCharsets.UTF_8);
      execute(container, initScriptPath, initScript);
    } catch (IOException e) {
      logger(container)
        .warn("Could not load classpath init script: {}", initScriptPath);
      throw new ScriptUtils.ScriptLoadException(
        "Could not load classpath init script: " + initScriptPath,
        e
      );
    } catch (ScriptException e) {
      logger(container)
        .error("Error while executing init script: {}", initScriptPath, e);
      throw new ScriptUtils.UncategorizedScriptException(
        "Error while executing init script: " + initScriptPath,
        e
      );
    }
  }

  protected abstract void execute(
    GenericContainer<?> container,
    String initScriptPath,
    String initScript
  )
    throws ScriptException;
}
