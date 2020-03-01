package io.github.nblxa.turntables.test.assertj;

import static io.github.nblxa.turntables.Turntables.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.assertj.TabAssert;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TestRowsByKeyAndColsByName {
  private static final String LS = System.lineSeparator();

  private static <T extends Tab> TabAssert<T> tabAssert(T expected, T actual) {
    return assertThat(actual)
        .colMode(Turntables.ColMode.MATCHES_BY_NAME)
        .rowMode(Turntables.RowMode.MATCHES_BY_KEY)
        .matchesExpected(expected);
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
    Assertions.assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("      c : 5").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4").append(LS)
            .append("      c : 6\">").append(LS).toString());
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
    Assertions.assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - b : 2").append(LS)
            .append("    - b : 4\">").append(LS).toString());
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
    Assertions.assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4").append(LS)
            .append("    - a : 5").append(LS)
            .append("      b : 6\">").append(LS).toString());
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
    Assertions.assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4\">").append(LS).toString());
  }
}
