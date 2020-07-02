package net.boeckling.turbocontainers.modules.cli;

import java.util.List;
import org.testcontainers.containers.Container;

public interface Executable {
  List<String> getCommands();

  default Container.ExecResult runIn(Container<?> container) {
    try {
      Container.ExecResult result = container.execInContainer(
        getCommands().toArray(new String[] {})
      );
      return throwOnFailure(result);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  default Container.ExecResult throwOnFailure(Container.ExecResult result) {
    if (result.getExitCode() != 0) {
      throw new RuntimeException(
        "failed to run script" +
        "\nout: " +
        result.getStdout() +
        "\nerr: " +
        result.getStderr()
      );
    }
    return result;
  }
}
