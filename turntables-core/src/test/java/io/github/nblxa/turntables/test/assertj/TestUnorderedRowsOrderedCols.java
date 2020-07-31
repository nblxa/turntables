package io.github.nblxa.turntables.test.assertj;

import static io.github.nblxa.turntables.Turntables.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.assertj.TabAssert;
import io.github.nblxa.turntables.assertj.assertj.AssertAssertJ;
import org.junit.Test;

public class TestUnorderedRowsOrderedCols {
  private static final String LS = System.lineSeparator();

  private static <T extends Tab> TabAssert<T> tabAssert(T expected, T actual) {
    return tabAsserter(actual)
        .matchesExpected(expected);
  }

  private static <T extends Tab> TabAssert<T> tabAsserter(T actual) {
    return assertThat(actual)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_ANY_ORDER);
  }

  @Test
  public void match() {
    Tab exp = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(3, 4)
        .row(1, 2);

    tabAssert(exp, act).matches();
  }

  @Test
  public void mismatch() {
    Tab exp = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(5, 4)
        .row(1, 2);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      tabAssert(exp, act);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 5").append(LS)
            .append("      col2 : 4").append(LS).toString());
  }

  @Test
  public void moreCols() {
    Tab exp = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(3, 6, 4)
        .row(1, 5, 2);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      tabAssert(exp, act);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 1").append(LS)
            .append("        col2 : 2").append(LS)
            .append("      - col1 : 3").append(LS)
            .append("        col2 : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col3").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 3").append(LS)
            .append("        col2 : 6").append(LS)
            .append("        col3 : 4").append(LS)
            .append("      - col1 : 1").append(LS)
            .append("        col2 : 5").append(LS)
            .append("        col3 : 2").append(LS).toString());
  }

  @Test
  public void fewerCols() {
    Tab exp = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(4)
        .row(2);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      tabAssert(exp, act);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 1").append(LS)
            .append("        col2 : 2").append(LS)
            .append("      - col1 : 3").append(LS)
            .append("        col2 : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 4").append(LS)
            .append("      - col1 : 2").append(LS).toString());
  }

  @Test
  public void moreRows() {
    Tab exp = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(3, 4)
        .row(1, 2)
        .row(5, 6);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      tabAssert(exp, act);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4").append(LS)
            .append("    - col1 : 5").append(LS)
            .append("      col2 : 6").append(LS).toString());
  }

  @Test
  public void fewerRows() {
    Tab exp = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(3, 4);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      tabAssert(exp, act);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4").append(LS).toString());
  }

  @Test
  public void typeMismatch() {
    Tab exp = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.STRING)
        .row(3, "4")
        .row(1, "2");

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      tabAssert(exp, act);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 1").append(LS)
            .append("        col2 : 2").append(LS)
            .append("      - col1 : 3").append(LS)
            .append("        col2 : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : col1").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : col2").append(LS)
            .append("        type : string").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - col1 : 3").append(LS)
            .append("        col2 : 4").append(LS)
            .append("      - col1 : 1").append(LS)
            .append("        col2 : 2").append(LS).toString());
  }

  @Test
  public void typeMismatchCaseInsensitive() {
    Tab exp = Turntables.tab()
        .col("A", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .col("a", Typ.INTEGER)
        .col("B", Typ.STRING)
        .row(1, "2")
        .row(3, "4");

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      tabAsserter(act)
          .nameMode(Settings.NameMode.CASE_INSENSITIVE)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : A").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : b").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - A : 1").append(LS)
            .append("        b : 2").append(LS)
            .append("      - A : 3").append(LS)
            .append("        b : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : A").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : b").append(LS)
            .append("        type : string").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - A : 1").append(LS)
            .append("        b : 2").append(LS)
            .append("      - A : 3").append(LS)
            .append("        b : 4").append(LS).toString());
  }
}
