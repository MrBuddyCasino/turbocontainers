package net.boeckling.turbocontainers.parameter;

public class ParameterResolutionFailedException extends RuntimeException {

  public ParameterResolutionFailedException(String message) {
    super(message);
  }

  public ParameterResolutionFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
