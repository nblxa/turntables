package io.github.nblxa.turntables.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.exception.StructureException;
import org.junit.Test;

public class TestColsMatchByName {

  private boolean matchByName(Tab expected, Tab actual) {
    return AssertionProxy.builder()
        .expected(expected)
        .actual(actual)
        .colMode(Settings.ColMode.MATCH_BY_NAME)
        .buildOrGetActualProxy()
        .matchesExpected();
  }

  @Test
  public void matchesSame() {
    Tab actual = Turntables.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);

    boolean res = matchByName(actual, actual);
    assertThat(res).isTrue();
  }

  @Test
  public void matchesInSameOrder() {
    Tab expected = Turntables.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByName(expected, actual);
    assertThat(res).isTrue();
  }

  @Test
  public void duplicateColumnsInExpected() {
    Tab expected = Turntables.tab()
        .col("A", Typ.INTEGER)
        .col("A", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    Throwable t = catchThrowable(() -> matchByName(expected, actual));
    assertThat(t)
        .isInstanceOf(StructureException.class)
        .hasMessage("Duplicate column names in expected: A");
  }

  @Test
  public void duplicateColumnsInActual() {
    Tab expected = Turntables.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .col("B", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    Throwable t = catchThrowable(() -> matchByName(expected, actual));
    assertThat(t)
        .isInstanceOf(StructureException.class)
        .hasMessage("Duplicate column names in actual: B");
  }

  @Test
  public void namesMismatch() {
    Tab expected = Turntables.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .col("C", Typ.INTEGER)
        .col("D", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByName(expected, actual);
    assertThat(res).isFalse();
  }
}
