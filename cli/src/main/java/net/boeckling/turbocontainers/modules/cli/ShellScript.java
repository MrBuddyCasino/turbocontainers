package net.boeckling.turbocontainers.modules.cli;

public class ShellScript {
  public static final Script SH = Script.of("/bin/sh", "-c");

  public static Script of(Script script) {
    return SH.concat(script);
  }
}
