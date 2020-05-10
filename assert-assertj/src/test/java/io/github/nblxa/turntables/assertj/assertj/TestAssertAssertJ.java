package io.github.nblxa.turntables.assertj.assertj;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.Test;

public class TestAssertAssertJ {
  private static final String LS = System.lineSeparator();

  @Test
  public void keywordReplacement() {
    String input = new StringBuilder()
        .append("Something Something").append(LS)
        .append("EXPECTED: Table:").append(LS)
        .append("    - col1 : 1").append(LS)
        .append("      col2 : 2").append(LS)
        .append("BUT: WAS Table:").append(LS)
        .append("    - col1 : 3").append(LS)
        .append("      col2 : 4").append(LS)
        .append("Something Something").toString();

    String expected = new StringBuilder()
        .append("Something Something").append(LS)
        .append("{{MSG_EXP}}Table:").append(LS)
        .append("    - col1 : 1").append(LS)
        .append("      col2 : 2").append(LS)
        .append("{{MSG_WAS}}Table:").append(LS)
        .append("    - col1 : 3").append(LS)
        .append("      col2 : 4").append(LS)
        .append("Something Something").toString();

    String actual = AssertionErrorAssert.replaceAssertionKeywords(input);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void keywordsNotFound() {
    String input = new StringBuilder()
        .append("Something Something").append(LS)
        .append("EXPECTED: Table:").append(LS)
        .append("    - col1 : 1").append(LS)
        .append("      col2 : 2").append(LS)
        .append("BUT NOT FOUND").toString();

    String actual = AssertionErrorAssert.replaceAssertionKeywords(input);

    assertThat(actual).isEqualTo(input);
  }

  @Test
  public void throwableIsNotAssertionError() {
    Throwable actual = new RuntimeException("boom");
    Throwable t = catchThrowable(
        () -> AssertAssertJ.assertThat(actual)
            .isAssertionErrorWithMessage("boom"));
    assertThat(t)
        .isExactlyInstanceOf(AssertionError.class)
        .hasMessageContaining("Expecting:")
        .hasMessageContaining("<java.lang.RuntimeException: boom>")
        .hasMessageContaining("to be an instance of:")
        .hasMessageContaining("<java.lang.AssertionError>");
  }

  @Test
  public void throwableMessage() {
    Throwable actual = new AssertionError(new StringBuilder(LS)
        .append("EXPECTED: Table:").append(LS)
        .append("    - flower : roses").append(LS)
        .append("      colour : red").append(LS)
        .append("BUT: WAS Table:").append(LS)
        .append("    - flower : cacti").append(LS)
        .append("      colour : green")
        .toString());
    Throwable t = catchThrowable(
        () -> AssertAssertJ.assertThat(actual)
            .isAssertionErrorWithMessage(new StringBuilder(LS)
                .append("EXPECTED: Table:").append(LS)
                .append("    - flower : roses").append(LS)
                .append("      colour : red").append(LS)
                .append("BUT: WAS Table:").append(LS)
                .append("    - flower : violets").append(LS)
                .append("      colour : blue")
                .toString()));
    assertThat(t)
        .isExactlyInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Assertion error message mismatch.").append(LS)
            .append("Expected: \"").append(LS)
            .append("{{MSG_EXP}}Table:").append(LS)
            .append("    - flower : roses").append(LS)
            .append("      colour : red").append(LS)
            .append("{{MSG_WAS}}Table:").append(LS)
            .append("    - flower : violets").append(LS)
            .append("      colour : blue\"").append(LS)
            .append("But was: \"").append(LS)
            .append("{{MSG_EXP}}Table:").append(LS)
            .append("    - flower : roses").append(LS)
            .append("      colour : red").append(LS)
            .append("{{MSG_WAS}}Table:").append(LS)
            .append("    - flower : cacti").append(LS)
            .append("      colour : green\"").append(LS)
            .toString());
  }
}
