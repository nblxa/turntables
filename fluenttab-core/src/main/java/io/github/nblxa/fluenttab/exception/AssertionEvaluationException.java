package io.github.nblxa.fluenttab.exception;

public class AssertionEvaluationException extends UnsupportedOperationException {
  public AssertionEvaluationException() {
    super("Cannot evaluate a value containing an assertion.");
  }
}
