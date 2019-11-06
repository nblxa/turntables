package io.github.nblxa.turntables.exception;

public class AssertionEvaluationException extends UnsupportedOperationException {
  public AssertionEvaluationException() {
    super("Cannot evaluate a value containing an assertion.");
  }
}
