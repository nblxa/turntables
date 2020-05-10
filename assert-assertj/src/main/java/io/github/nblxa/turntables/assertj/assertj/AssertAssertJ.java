package io.github.nblxa.turntables.assertj.assertj;

public class AssertAssertJ {
  private AssertAssertJ() {
  }

  /**
   * API entry point for assertions on assertion errors.
   *
   * @param t throwable to run assertions on
   * @return the assertion object
   */
  public static AssertionErrorAssert assertThat(Throwable t) {
    return new AssertionErrorAssert(t);
  }
}
