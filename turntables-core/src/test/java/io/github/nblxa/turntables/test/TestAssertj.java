package io.github.nblxa.turntables.test;

import static io.github.nblxa.turntables.Turntables.*;
import static org.assertj.core.api.Assertions.*;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import org.junit.Test;

public class TestAssertj {
  private static final String LS = System.lineSeparator();

  @Test
  public void mismatchMatcher_colByOrder_bothUnnamed() {
    Tab exp = Turntables.tab()
        .row(testInt(i -> i > 10), 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      assertThat(act)
          .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
          .matches(exp);
      // comment next line to check in the IDE:
    });
    assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - col1 : java.util.function.IntPredicate").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4\">").append(LS).toString());
  }

  @Test
  public void mismatchMatcher_colByOrder_expectedNamed() {
    Tab exp = Turntables.tab()
        .col("a", Typ.INTEGER).col("b", Typ.INTEGER)
        .row(testInt(i -> i > 10), 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      assertThat(act)
          .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
          .matches(exp);
      // comment next line to check in the IDE:
    });

    // Assert that column names from the expected tab are also used in actual tab's representation
    // so that there is no diff in column names when we are matching by position and names
    // are irrelevant.
    assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - a : java.util.function.IntPredicate").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4\">").append(LS).toString());
  }

  @Test
  public void mismatchMatcher_colByOrder_actualNamed() {
    Tab exp = Turntables.tab()
        .row(testInt(i -> i > 10), 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .col("a", Typ.INTEGER).col("b", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      assertThat(act)
          .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
          .matches(exp);
      // comment next line to check in the IDE:
    });

    // Assert that column names from the actual tab are also used in expected tab's representation
    // so that there is no diff in column names when we are matching by position and names
    // are irrelevant.
    assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - a : java.util.function.IntPredicate").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4\">").append(LS).toString());
  }

  @Test
  public void match() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab exp = Turntables.tab()
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4);

    Throwable t = catchThrowable(() -> {
      assertThat(act)
          .matches(exp)
          .isNotEqualTo(exp);
    });
    assertThat(t).isNull();
  }
}
