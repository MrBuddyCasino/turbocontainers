package net.boeckling.turbocontainers.log;

import org.slf4j.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerLoggerFactory;

public class ContainerLogger {

  public static Logger logger(GenericContainer<?> container) {
    return DockerLoggerFactory.getLogger(container.getDockerImageName());
  }
}
