package io.github.nblxa.turntables.test.assertj;

import static io.github.nblxa.turntables.Turntables.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.assertj.TabAssert;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TestUnorderedRowsOrderedCols {
  private static final String LS = System.lineSeparator();

  private static <T extends Tab> TabAssert<T> tabAssert(T expected, T actual) {
    return assertThat(actual)
        .colMode(Turntables.ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER)
        .matchesExpected(expected);
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
    Assertions.assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 6").append(LS)
            .append("      col3 : 4").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 5").append(LS)
            .append("      col3 : 2\">").append(LS).toString());
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
    Assertions.assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - col1 : 4").append(LS)
            .append("    - col1 : 2\">").append(LS).toString());
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
    Assertions.assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4").append(LS)
            .append("    - col1 : 5").append(LS)
            .append("      col2 : 6\">").append(LS).toString());
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
    Assertions.assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4\">").append(LS).toString());
  }
}
