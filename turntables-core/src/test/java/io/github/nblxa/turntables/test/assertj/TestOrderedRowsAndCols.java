package io.github.nblxa.turntables.test.assertj;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.assertj.TabAssert;
import io.github.nblxa.turntables.assertj.assertj.AssertAssertJ;
import org.junit.Test;

import static io.github.nblxa.turntables.Turntables.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TestOrderedRowsAndCols {
  private static final String LS = System.lineSeparator();

  private static <T extends Tab> TabAssert<T> tabAssert(T expected, T actual) {
    return assertThat(actual)
        .colMode(Turntables.ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(Turntables.RowMode.MATCHES_IN_GIVEN_ORDER)
        .matchesExpected(expected);
  }

  @Test
  public void match() {
    Tab exp = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    tabAssert(exp, act).matches();
  }

  @Test
  public void moreCols() {
    Tab exp = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(1, 5, 2)
        .row(3, 6, 4);

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
            .append("      - col1 : 1").append(LS)
            .append("        col2 : 5").append(LS)
            .append("        col3 : 2").append(LS)
            .append("      - col1 : 3").append(LS)
            .append("        col2 : 6").append(LS)
            .append("        col3 : 4").append(LS).toString());
  }

  @Test
  public void fewerCols() {
    Tab exp = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(2)
        .row(4);

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
            .append("      - col1 : 2").append(LS)
            .append("      - col1 : 4").append(LS).toString());
  }

  @Test
  public void moreRows() {
    Tab exp = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(5, 6)
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
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 5").append(LS)
            .append("      col2 : 6").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4").append(LS).toString());
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
}
