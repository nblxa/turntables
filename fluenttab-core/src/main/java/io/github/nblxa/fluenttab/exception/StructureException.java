package io.github.nblxa.fluenttab.exception;

public class StructureException extends RuntimeException {
  public StructureException(String message) {
    super(message);
  }

  public StructureException(String message, Throwable cause) {
    super(message, cause);
  }
}
