package io.github.nblxa.turntables.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import org.junit.Test;

public class TestRowsMatchByKey {

  private AssertionProxy.Builder matchByKey(Tab expected, Tab actual) {
    return AssertionProxy.builder()
        .expected(expected)
        .actual(actual)
        .rowMode(Turntables.RowMode.MATCHES_BY_KEY);
  }

  @Test
  public void matchesSame() {
    Tab actual = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);

    boolean res = matchByKey(actual, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void matchesInSameOrder() {
    Tab expected = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void matchesInDifferentOrder() {
    Tab expected = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(5, 6).row(1, 2);
    Tab actual = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void mismatchInKey() {
    Tab expected = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .key("K", Typ.INTEGER)
        .key("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void mismatchInKeyOneIsEmpty() {
    Tab expected = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .col("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void matchesSameKeyIsEmpty() {
    Tab expected = Turntables.tab()
        .col("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .col("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void matchesReversedKeyIsEmpty() {
    Tab expected = Turntables.tab()
        .col("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .col("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(5, 6).row(1, 2);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void matchesReversedWithMatchersKeyIsEmpty() {
    Tab expected = Turntables.tab()
        .col("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, Turntables.test(i -> true)).row(5, 6);
    Tab actual = Turntables.tab()
        .col("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(5, 6).row(1, 2);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void matchesReversedWholeTabIsKey() {
    Tab expected = Turntables.tab()
        .key("K", Typ.INTEGER)
        .key("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .key("K", Typ.INTEGER)
        .key("V", Typ.INTEGER)
        .row(5, 6).row(1, 2);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void matchesReversedKeyByName() {
    Tab expected = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(5, 6).row(1, 2);

    boolean res = matchByKey(expected, actual)
        .colMode(Turntables.ColMode.MATCHES_BY_NAME)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void matchesReversedKeyByNameWithColumnsReordered() {
    Tab expected = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .col("V", Typ.INTEGER)
        .key("K", Typ.INTEGER)
        .row(6, 5).row(2, 1);

    boolean res = matchByKey(expected, actual)
        .colMode(Turntables.ColMode.MATCHES_BY_NAME)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void matchesReversedKeyByNameWithColumnsReorderedWithMatchers() {
    Tab expected = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(1, Turntables.test(i -> true)).row(5, 6);
    Tab actual = Turntables.tab()
        .col("V", Typ.INTEGER)
        .key("K", Typ.INTEGER)
        .row(6, 5).row(2, 1);

    boolean res = matchByKey(expected, actual)
        .colMode(Turntables.ColMode.MATCHES_BY_NAME)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void noMatchReversedKeyByNameWithColumnsReorderedWithMatchers() {
    Tab expected = Turntables.tab()
        .key("K", Typ.INTEGER)
        .col("V", Typ.INTEGER)
        .row(2, Turntables.test(i -> true)).row(5, 6);
    Tab actual = Turntables.tab()
        .col("V", Typ.INTEGER)
        .key("K", Typ.INTEGER)
        .row(6, 5).row(2, 1);

    boolean res = matchByKey(expected, actual)
        .colMode(Turntables.ColMode.MATCHES_BY_NAME)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void unnamedMatchesSame() {
    Tab actual = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(1, 2)
        .row(3, 4);

    boolean res = matchByKey(actual, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void unnamedMatchesInSameOrder() {
    Tab expected = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void unnamedMatchesInDifferentOrder() {
    Tab expected = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(5, 6).row(1, 2);
    Tab actual = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void unnamedMismatchInKeyCardinality() {
    Tab expected = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .key(Typ.INTEGER)
        .key(Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void unnamedMismatchInKeyCols() {
    Tab expected = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(1, 2).row(5, 6);
    Tab actual = Turntables.tab()
        .col(Typ.INTEGER)
        .key(Typ.INTEGER)
        .row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void unnamedMatchesWithDuplicates() {
    Tab expected = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(1, 2).row(5, 6).row(5, 7);
    Tab actual = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(5, 7).row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void unnamedMatchesWithIdenticalDuplicates() {
    Tab expected = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(1, 2).row(5, 6).row(5, 6);
    Tab actual = Turntables.tab()
        .key(Typ.INTEGER)
        .col(Typ.INTEGER)
        .row(5, 6).row(1, 2).row(5, 6);

    boolean res = matchByKey(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }
}
