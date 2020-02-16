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
          .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
          .matchesExpected(exp);
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
          .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
          .matchesExpected(exp);
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
          .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
          .matchesExpected(exp);
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
  public void mismatchMatcher_colByOrder_bothNamed() {
    Tab exp = Turntables.tab()
        .col("x", Typ.INTEGER).col("y", Typ.INTEGER)
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
          .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });

    // Assert that column names from the actual tab are also used in expected tab's representation
    // so that there is no diff in column names when we are matching by position and names
    // are irrelevant.
    assertThat(t)
        .isInstanceOf(AssertionError.class)
        .hasMessage(new StringBuilder(LS)
            .append("Expected: <\"Table:").append(LS)
            .append("    - x : java.util.function.IntPredicate").append(LS)
            .append("      y : 2").append(LS)
            .append("    - x : 3").append(LS)
            .append("      y : 4\">").append(LS)
            .append("but was: <\"Table:").append(LS)
            .append("    - x : 1").append(LS)
            .append("      y : 2").append(LS)
            .append("    - x : 3").append(LS)
            .append("      y : 4\">").append(LS).toString());
  }

  @Test
  public void match() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);
    Tab exp = Turntables.tab()
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4);

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matchesExpected(exp)
        .isNotEqualTo(exp);
  }

  @Test
  public void matchFluent_rows() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(new Object());
  }

  @Test
  public void matchFluent_unnamedColsRows() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .col(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(new Object());
  }

  @Test
  public void matchFluent_namedColsRows() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .col("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(new Object());
  }

  @Test
  public void matchFluent_unnamedCols() {
    Tab act = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.INTEGER);

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .col(Typ.INTEGER)
        .col(Typ.INTEGER)
        .asExpected()
        .isNotEqualTo(new Object());
  }

  @Test
  public void matchFluent_namedCols() {
    Tab act = Turntables.tab()
        .col("a", Typ.INTEGER)
        .col("b", Typ.INTEGER);

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .col("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .asExpected()
        .isNotEqualTo(new Object());
  }

  @Test
  public void matchFluent_unnamedKeyColsRows() {
    Tab act = Turntables.tab()
        .row(1, 2, "x")
        .row(3, 4, "y");

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .key(Typ.INTEGER)
        .key(Typ.INTEGER)
        .col(Typ.STRING)
        .row(testInt(i -> i <= 10), 2, "x")
        .row(3, 4, "y")
        .asExpected()
        .isNotEqualTo(new Object());
  }

  @Test
  public void matchFluent_namedKeyColsRows() {
    Tab act = Turntables.tab()
        .row(1, 2, "x")
        .row(3, 4, "y");

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .key("a", Typ.INTEGER)
        .key("b", Typ.INTEGER)
        .col("c", Typ.STRING)
        .row(testInt(i -> i <= 10), 2, "x")
        .row(3, 4, "y")
        .asExpected()
        .isNotEqualTo(new Object());
  }

  @Test
  public void matchFluent_unnamedKeyCols() {
    Tab act = Turntables.tab()
        .key(Typ.INTEGER)
        .key(Typ.INTEGER)
        .col(Typ.STRING);

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .key(Typ.INTEGER)
        .key(Typ.INTEGER)
        .col(Typ.STRING)
        .asExpected()
        .isNotEqualTo(new Object());
  }

  @Test
  public void matchFluent_namedKeyCols() {
    Tab act = Turntables.tab()
        .key("a", Typ.INTEGER)
        .key("b", Typ.INTEGER)
        .col("c", Typ.STRING);

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .key("a", Typ.INTEGER)
        .key("b", Typ.INTEGER)
        .col("c", Typ.STRING)
        .asExpected()
        .isNotEqualTo(new Object());
  }

  @Test
  public void matchFluent_rowsTab_throwsUoe() {
    Tab act = Turntables.tab();

    Throwable t = catchThrowable(() ->
        assertThat(act)
            .matches()
            .row(1)
            .tab());
    assertThat(t)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasNoCause();
  }

  @Test
  public void matchFluent_unnamedColsRowsTab_throwsUoe() {
    Tab act = Turntables.tab();

    Throwable t = catchThrowable(() ->
        assertThat(act)
            .matches()
            .col(Typ.INTEGER)
            .row(1)
            .tab());
    assertThat(t)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasNoCause();
  }

  @Test
  public void matchFluent_namedColsRowsTab_throwsUoe() {
    Tab act = Turntables.tab();

    Throwable t = catchThrowable(() ->
        assertThat(act)
            .matches()
            .col("a", Typ.INTEGER)
            .row(1)
            .tab());
    assertThat(t)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasNoCause();
  }

  @Test
  public void matchFluent_unnamedColsTab_throwsUoe() {
    Tab act = Turntables.tab();

    Throwable t = catchThrowable(() ->
        assertThat(act)
            .matches()
            .col(Typ.INTEGER)
            .tab());
    assertThat(t)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasNoCause();
  }

  @Test
  public void matchFluent_namedColsTab_throwsUoe() {
    Tab act = Turntables.tab();

    Throwable t = catchThrowable(() ->
        assertThat(act)
            .matches()
            .col("a", Typ.INTEGER)
            .tab());
    assertThat(t)
        .isInstanceOf(UnsupportedOperationException.class)
        .hasNoCause();
  }

  @Test
  public void matchFluent_rows_rowAdder() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    Throwable t = catchThrowable(() ->
      assertThat(act)
          .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
          .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
          .matches()
          .rowAdder());
    assertThat(t)
        .isInstanceOf(IllegalStateException.class)
        .hasNoCause();
  }

  @Test
  public void matchFluent_unnamedColsRows_rowAdder() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .col(Typ.INTEGER)
        .col(Typ.INTEGER)
        .rowAdder()
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(new Object());
  }

  @Test
  public void matchFluent_namedColsRows_rowAdder() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    assertThat(act)
        .colMode(ColMode.MATCHES_IN_GIVEN_ORDER)
        .rowMode(RowMode.MATCHES_IN_GIVEN_ORDER)
        .matches()
        .col("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .rowAdder()
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(new Object());
  }
}
