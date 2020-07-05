package net.boeckling.turbocontainers.script;

import org.testcontainers.containers.GenericContainer;

public interface ScriptRunner extends Comparable<ScriptRunner> {
  void runScript(GenericContainer<?> container, String initScriptPath);

  default int getPriority() {
    return 0;
  }

  default int compareTo(ScriptRunner o) {
    return Integer.compare(getPriority(), o.getPriority());
  }
}
