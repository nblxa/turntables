package io.github.nblxa.fluenttab.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;
import io.github.nblxa.fluenttab.Typ;
import io.github.nblxa.fluenttab.exception.StructureException;
import org.junit.Test;

public class TestColsMatchByName {

  private AssertionProxy.Builder matchByName(Tab expected, Tab actual) {
    return AssertionProxy.builder()
        .expected(expected)
        .actual(actual)
        .colMode(FluentTab.ColMode.MATCHES_BY_NAME);
  }

  @Test
  public void matchesSame() {
    Tab actual = FluentTab.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);

    boolean res = matchByName(actual, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void matchesInSameOrder() {
    Tab expected = FluentTab.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = FluentTab.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByName(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void duplicateColumnsInExpected() {
    Tab expected = FluentTab.tab()
        .col("A", Typ.INTEGER)
        .col("A", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = FluentTab.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    Throwable t = catchThrowable(() -> {
      matchByName(expected, actual)
          .buildOrGetActualProxy()
          .matchesExpected();
    });
    assertThat(t)
        .isInstanceOf(StructureException.class)
        .hasMessage("Duplicate column names in expected: A");
  }

  @Test
  public void duplicateColumnsInActual() {
    Tab expected = FluentTab.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = FluentTab.tab()
        .col("B", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    Throwable t = catchThrowable(() -> {
      matchByName(expected, actual)
          .buildOrGetActualProxy()
          .matchesExpected();
    });
    assertThat(t)
        .isInstanceOf(StructureException.class)
        .hasMessage("Duplicate column names in actual: B");
  }

  @Test
  public void namesMismatch() {
    Tab expected = FluentTab.tab()
        .col("A", Typ.INTEGER)
        .col("B", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = FluentTab.tab()
        .col("C", Typ.INTEGER)
        .col("D", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByName(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

}
