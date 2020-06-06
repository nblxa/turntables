package io.github.nblxa.turntables.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;
import org.junit.Test;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;

public class TestAssertions {
  private AssertionProxy.Builder assertionProxy(Tab expected, Tab actual) {
    return AssertionProxy.builder()
        .expected(expected)
        .actual(actual)
        .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER);
  }

  @Test
  public void intMatches() {
    Tab expected = Turntables.tab().row(Turntables.testInt(i -> i % 2 == 0));
    Tab actual = Turntables.tab().row(42);

    boolean match = assertionProxy(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(match)
        .isTrue();
  }

  @Test
  public void intDoesNotMatch() {
    Tab expected = Turntables.tab().row(Turntables.testInt(i -> i % 2 == 0));
    Tab actual = Turntables.tab().row(101);

    boolean match = assertionProxy(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(match)
        .isFalse();
  }

  @Test
  public void longMatches() {
    Tab expected = Turntables.tab().row(Turntables.testLong(l -> l % 2L == 0L));
    Tab actual = Turntables.tab().row(42L);

    boolean match = assertionProxy(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(match)
        .isTrue();
  }

  @Test
  public void longDoesNotMatch() {
    Tab expected = Turntables.tab().row(Turntables.testLong(l -> l % 2L == 0L));
    Tab actual = Turntables.tab().row(101L);

    boolean match = assertionProxy(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(match)
        .isFalse();
  }

  @Test
  public void doubleMatches() {
    Tab expected = Turntables.tab().row(Turntables.testDouble(d -> d * 2 == 2.0d));
    Tab actual = Turntables.tab().row(1.0d);

    boolean match = assertionProxy(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(match)
        .isTrue();
  }

  @Test
  public void doubleDoesNotMatch() {
    Tab expected = Turntables.tab().row(Turntables.testDouble(d -> d * 2 == 2.0d));
    Tab actual = Turntables.tab().row(3.14d);

    boolean match = assertionProxy(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(match)
        .isFalse();
  }

  @Test
  public void stringMatches() {
    Tab expected = Turntables.tab().row(Pattern.compile("[a-z]+"));
    Tab actual = Turntables.tab().row("tanaka");

    boolean match = assertionProxy(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(match)
        .isTrue();
  }

  @Test
  public void stringDoesNotMatch() {
    Tab expected = Turntables.tab().row(Pattern.compile("[a-z]+"));
    Tab actual = Turntables.tab().row("Tanakas!");

    boolean match = assertionProxy(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(match)
        .isFalse();
  }
}
