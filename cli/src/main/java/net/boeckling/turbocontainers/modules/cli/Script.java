package net.boeckling.turbocontainers.modules.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class Script implements Executable {
  private final List<String> commands = new ArrayList<>();

  private Script(List<String> commands) {
    this.commands.addAll(commands);
  }

  public String joinCommands(String delim) {
    StringJoiner joiner = new StringJoiner(delim);
    for (CharSequence command : commands) {
      joiner.add(command);
    }
    return joiner.toString();
  }

  public Script concat(Script... scripts) {
    List<String> commands = new ArrayList<>(getCommands());
    for (Script script : scripts) {
      commands.addAll(script.getCommands());
    }

    return new Script(commands);
  }

  public static Script of(String... command) {
    return new Script(Arrays.asList(command));
  }

  public static Script of(Path path) {
    try {
      String content = new String(Files.readAllBytes(path));
      return of(content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<String> getCommands() {
    return commands;
  }
}
