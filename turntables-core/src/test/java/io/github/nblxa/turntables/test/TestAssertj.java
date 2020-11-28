package io.github.nblxa.turntables.test;

import static io.github.nblxa.turntables.Turntables.*;
import static org.assertj.core.api.Assertions.*;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.assertj.assertj.AssertAssertJ;
import org.junit.Test;

public class TestAssertj {
  private static final String LS = System.lineSeparator();
  private static final Object SOME_RANDOM_OBJECT = new Object();

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
          .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
          .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - col1 : java.util.function.IntPredicate").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - col1 : 1").append(LS)
            .append("      col2 : 2").append(LS)
            .append("    - col1 : 3").append(LS)
            .append("      col2 : 4"));
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
          .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
          .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });

    // Assert that column names from the expected tab are also used in actual tab's representation
    // so that there is no diff in column names when we are matching by position and names
    // are irrelevant.
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - a : java.util.function.IntPredicate").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4"));
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
          .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
          .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });

    // Assert that column names from the actual tab are also used in expected tab's representation
    // so that there is no diff in column names when we are matching by position and names
    // are irrelevant.
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - a : java.util.function.IntPredicate").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - a : 1").append(LS)
            .append("      b : 2").append(LS)
            .append("    - a : 3").append(LS)
            .append("      b : 4"));
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
          .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
          .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
          .matchesExpected(exp);
      // comment next line to check in the IDE:
    });

    // Assert that column names from the actual tab are also used in expected tab's representation
    // so that there is no diff in column names when we are matching by position and names
    // are irrelevant.
    AssertAssertJ.assertThat(t)
        .isAssertionErrorWithMessage(new StringBuilder(LS)
            .append("EXPECTED: Table:").append(LS)
            .append("    - x : java.util.function.IntPredicate").append(LS)
            .append("      y : 2").append(LS)
            .append("    - x : 3").append(LS)
            .append("      y : 4").append(LS)
            .append("BUT: WAS Table:").append(LS)
            .append("    - x : 1").append(LS)
            .append("      y : 2").append(LS)
            .append("    - x : 3").append(LS)
            .append("      y : 4"));
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
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matchesExpected(exp)
        .isNotEqualTo(exp);
  }

  @Test
  public void matchFluent_rows() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_unnamedColsRows() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .col(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_namedColsRows() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .col("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_unnamedCols() {
    Tab act = Turntables.tab()
        .col(Typ.INTEGER)
        .col(Typ.INTEGER);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .col(Typ.INTEGER)
        .col(Typ.INTEGER)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_namedCols() {
    Tab act = Turntables.tab()
        .col("a", Typ.INTEGER)
        .col("b", Typ.INTEGER);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .col("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_unnamedKeyColsRows1() {
    Tab act = Turntables.tab()
        .row(1, 2, "x")
        .row(3, 4, "y");

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .key(Typ.INTEGER)
        .key(Typ.INTEGER)
        .col(Typ.STRING)
        .row(testInt(i -> i <= 10), 2, "x")
        .row(3, 4, "y")
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_unnamedKeyColsRows2() {
    Tab act = Turntables.tab()
        .row("x", 1, 2)
        .row("y", 3, 4);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .col(Typ.STRING)
        .key(Typ.INTEGER)
        .key(Typ.INTEGER)
        .row("x", testInt(i -> i <= 10), 2)
        .row("y", 3, 4)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_namedKeyColsRows1() {
    Tab act = Turntables.tab()
        .row(1, 2, "x")
        .row(3, 4, "y");

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .key("a", Typ.INTEGER)
        .key("b", Typ.INTEGER)
        .col("c", Typ.STRING)
        .row(testInt(i -> i <= 10), 2, "x")
        .row(3, 4, "y")
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_namedKeyColsRows2() {
    Tab act = Turntables.tab()
        .row("x", 1, 2)
        .row("y", 3, 4);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .col("c", Typ.STRING)
        .key("a", Typ.INTEGER)
        .key("b", Typ.INTEGER)
        .row("x", testInt(i -> i <= 10), 2)
        .row("y", 3, 4)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_unnamedKeyCols() {
    Tab act = Turntables.tab()
        .key(Typ.INTEGER)
        .key(Typ.INTEGER)
        .col(Typ.STRING);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .key(Typ.INTEGER)
        .key(Typ.INTEGER)
        .col(Typ.STRING)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_namedKeyCols() {
    Tab act = Turntables.tab()
        .key("a", Typ.INTEGER)
        .key("b", Typ.INTEGER)
        .col("c", Typ.STRING);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .key("a", Typ.INTEGER)
        .key("b", Typ.INTEGER)
        .col("c", Typ.STRING)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
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

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .rowAdder()
        .row(1, 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_unnamedColsRows_rowAdder() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .col(Typ.INTEGER)
        .col(Typ.INTEGER)
        .rowAdder()
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_namedColsRows_rowAdder() {
    Tab act = Turntables.tab()
        .row(1, 2)
        .row(3, 4);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .col("a", Typ.INTEGER)
        .col("b", Typ.INTEGER)
        .rowAdder()
        .row(testInt(i -> i <= 10), 2)
        .row(3, 4)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_empty() {
    Tab act = Turntables.tab();
    
    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_emptyUnnamed() {
    Tab act = Turntables.tab()
        .col(Typ.DATE);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .col(Typ.DATE)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_emptyNamed() {
    Tab act = Turntables.tab()
        .col("Wow", Typ.DATE);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .col("Wow", Typ.DATE)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_emptyUnnamedKey() {
    Tab act = Turntables.tab()
        .key(Typ.DATE);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .key(Typ.DATE)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }

  @Test
  public void matchFluent_emptyNamedKey() {
    Tab act = Turntables.tab()
        .key("Wow", Typ.DATE);

    assertThat(act)
        .colMode(Settings.ColMode.MATCH_IN_GIVEN_ORDER)
        .rowMode(Settings.RowMode.MATCH_IN_GIVEN_ORDER)
        .matches()
        .key("Wow", Typ.DATE)
        .asExpected()
        .isNotEqualTo(SOME_RANDOM_OBJECT);
  }
}
