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

public class TestRowsByKeyAndColsByName {
  private static final String LS = System.lineSeparator();

  private static <T extends Tab> TabAssert<T> tabAssert(T expected, T actual) {
    return tabAsserter(actual)
        .matchesExpected(expected);
  }

  private static <T extends Tab> TabAssert<T> tabAsserter(T actual) {
    return assertThat(actual)
        .colMode(Settings.ColMode.MATCH_BY_NAME)
        .rowMode(Settings.RowMode.MATCH_BY_KEY);
  }

  @Test
  public void match() {
    Tab exp = Turntables.tab()
        .key("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .col("b", Typ.INTEGER)
        .key("a", Typ.INTEGER)
        .row(4, 3)
        .row(2, 1);

    tabAssert(exp, act).matches();
  }

  @Test
  public void mismatch() {
    Tab exp = Turntables.tab()
        .key("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .col("b", Typ.INTEGER)
        .key("a", Typ.INTEGER)
        .row(2, 1)
        .row(5, 3);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      tabAssert(exp, act);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 5").append(LS).toString());
  }

  @Test
  public void moreCols() {
    Tab exp = Turntables.tab()
        .key("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .col("c", Typ.INTEGER)
        .key("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(6, 3, 4)
        .row(5, 1, 2);

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
            .append("      - name : a").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : true").append(LS)
            .append("      - name : b").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - a : 1").append(LS)
            .append("        b : 2").append(LS)
            .append("      - a : 3").append(LS)
            .append("        b : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : a").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : true").append(LS)
            .append("      - name : b").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : c").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - a : 1").append(LS)
            .append("        b : 2").append(LS)
            .append("        c : 5").append(LS)
            .append("      - a : 3").append(LS)
            .append("        b : 4").append(LS)
            .append("        c : 6").append(LS).toString());
  }

  @Test
  public void fewerCols() {
    Tab exp = Turntables.tab()
        .col("a", Typ.INTEGER)
        .key("b", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .key("b", Typ.INTEGER)
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
            .append("      - name : a").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("      - name : b").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : true").append(LS)
            .append("  rows:").append(LS)
            .append("      - a : 1").append(LS)
            .append("        b : 2").append(LS)
            .append("      - a : 3").append(LS)
            .append("        b : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : b").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : true").append(LS)
            .append("  rows:").append(LS)
            .append("      - b : 2").append(LS)
            .append("      - b : 4").append(LS).toString());
  }

  @Test
  public void moreRows() {
    Tab exp = Turntables.tab()
        .key("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .col("b", Typ.INTEGER)
        .key("a", Typ.INTEGER)
        .row(4, 3)
        .row(2, 1)
        .row(6, 5);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      tabAssert(exp, act);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4").append(LS)
            .append("    - a : 5").append(LS)
            .append("      b : 6").append(LS).toString());
  }

  @Test
  public void fewerRows() {
    Tab exp = Turntables.tab()
        .key("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .col("b", Typ.INTEGER)
        .key("a", Typ.INTEGER)
        .row(4, 3);

    Throwable t = null;
    // comment next line to check in the IDE:
    t = catchThrowable(() -> {
      tabAssert(exp, act);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4").append(LS).toString());
  }

  @Test
  public void typeMismatch() {
    Tab exp = Turntables.tab()
        .key("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .col("b", Typ.STRING)
        .key("a", Typ.INTEGER)
        .row("4", 3)
        .row("2", 1);

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
            .append("      - name : a").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : true").append(LS)
            .append("      - name : b").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - a : 1").append(LS)
            .append("        b : 2").append(LS)
            .append("      - a : 3").append(LS)
            .append("        b : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("  cols:").append(LS)
            .append("      - name : a").append(LS)
            .append("        type : integer").append(LS)
            .append("        key  : true").append(LS)
            .append("      - name : b").append(LS)
            .append("        type : string").append(LS)
            .append("        key  : false").append(LS)
            .append("  rows:").append(LS)
            .append("      - a : 1").append(LS)
            .append("        b : 2").append(LS)
            .append("      - a : 3").append(LS)
            .append("        b : 4").append(LS).toString());
  }

  @Test
  public void typeMismatchCaseInsensitive() {
    Tab exp = Turntables.tab()
        .key("A", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .col("B", Typ.STRING)
        .key("a", Typ.INTEGER)
        .row("4", 3)
        .row("2", 1);

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
            .append("        key  : true").append(LS)
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
            .append("        key  : true").append(LS)
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
