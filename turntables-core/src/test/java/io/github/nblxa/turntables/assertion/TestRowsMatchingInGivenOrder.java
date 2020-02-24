package io.github.nblxa.turntables.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import org.junit.Test;

public class TestRowsMatchingInGivenOrder {
  private AssertionProxy.Builder matchInAnyOrder(Tab expected, Tab actual) {
    return AssertionProxy.builder()
        .expected(expected)
        .actual(actual)
        .rowMode(Turntables.RowMode.MATCHES_IN_GIVEN_ORDER);
  }

  @Test
  public void matchesSame() {
    Tab expected = ActualExpected.tab_2x2();
    Tab actual = expected;

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void matchesInSameOrder() {
    Tab expected = Turntables.tab().row(1, 2).row(5, 6);
    Tab actual = Turntables.tab().row(1, 2).row(5, 6);

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void mismatchInDifferentOrder() {
    Tab expected = ActualExpected.tab_2x2_reversed();
    Tab actual = ActualExpected.tab_2x2();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }
}
