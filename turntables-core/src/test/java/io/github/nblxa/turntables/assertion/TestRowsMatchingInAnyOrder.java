package io.github.nblxa.turntables.assertion;

import static io.github.nblxa.turntables.Turntables.test;
import static io.github.nblxa.turntables.Turntables.testInt;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import org.junit.Ignore;
import org.junit.Test;

public class TestRowsMatchingInAnyOrder {
  private static final String LS = System.lineSeparator();

  private AssertionProxy.Builder matchInAnyOrder(Tab expected, Tab actual) {
    return AssertionProxy.builder()
        .expected(expected)
        .actual(actual)
        .rowMode(Turntables.RowMode.MATCHES_IN_ANY_ORDER);
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
  public void matchesInDifferentOrder() {
    Tab expected = ActualExpected.tab_2x2_reversed();
    Tab actual = ActualExpected.tab_2x2();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void withAssertions_matchesInSameOrder() {
    Tab expected = ActualExpected.expected_2x2_assertions();
    Tab actual = ActualExpected.tab_2x2();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void withAssertions_matchesInDifferentOrder() {
    Tab expected = ActualExpected.expected_2x2_assertions();
    Tab actual = ActualExpected.tab_2x2();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void notMatchingReversed() {
    Tab expected = ActualExpected.tab_2x2_reversed();
    Tab actual = ActualExpected.actual_2x2_doesnt_match();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void withAssertions_mismatch() {
    Tab actual = ActualExpected.actual_2x2_doesnt_match();
    Tab expected = ActualExpected.expected_2x2_assertions();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void withAssertions_rowCountMismatch() {
    Tab actual = ActualExpected.tab_1x2();
    Tab expected = ActualExpected.tab_2x2_reversed();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void empty_matchesEmpty() {
    Tab actual = Turntables.tab();
    Tab expected = Turntables.tab();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void empty_doesntMatchNull() {
    Tab actual = Turntables.tab().row(null);
    Tab expected = Turntables.tab();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void withAssertions_matchesBig() {
    Tab actual = ActualExpected.actual_10x5();
    Tab expected = ActualExpected.expected_10x5_assertions();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void withAssertions_bigFail() {
    Tab expected = ActualExpected.expected_10x5_assertions();
    Tab actual = ActualExpected.actual_10x5_doesnt_match();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void withAssertions_matchesDuplicates() {
    Tab expected = ActualExpected.expected_2x2_assertions();
    Tab actual = ActualExpected.tab_2x2_duplicates();

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void withAssertions_oneActualMatchesBothExpected_theOtherDoesnt_fails() {
    IntPredicate test = testInt(i -> i == 1 || i == 2);
    Tab expected = Turntables.tab().row(test, test).row(test, test);
    Tab actual = Turntables.tab().row(2, 1).row(3, 3);

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void withAssertions_onlyOneActual_matchingBothExpected() {
    IntPredicate test = testInt(i -> i == 1 || i == 2);
    Tab expected = Turntables.tab().row(test, test).row(test, test);
    Tab actual = Turntables.tab().row(2, 1);

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void withAssertions_twoEqualActuals_matchingOneOfTwoExpected() {
    IntPredicate test = testInt(i -> i == 1 || i == 2);
    Tab expected = Turntables.tab().row(test, test).row(0, test);
    Tab actual = Turntables.tab().row(1, 2).row(1, 2);

    Set<Tab.Row> distinctExp = new HashSet<>(expected.rows());
    Set<Tab.Row> distinctAct = new HashSet<>(actual.rows());
    assertThat(distinctExp).hasSize(2);
    assertThat(distinctAct).hasSize(1);
    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void assertionsOrderCanCauseMismatch_assumingActualSortedDesc_matches() {
    Tab expected = Turntables.tab().row(test(o -> true)).row(testInt(i -> i == 100));
    Tab actual = Turntables.tab().row(100).row(2);

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void assertionsOrderCanCauseMismatch_assumingActualSortedAsc_matches() {
    Tab expected = Turntables.tab().row(test(o -> true)).row(testInt(i -> i == 2));
    Tab actual = Turntables.tab().row(2).row(100);

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void assertionsOrderCanBlowUpPermutations_assumingActualSortedDesc_matches() {
    Predicate anything = test(o -> true);
    Tab expected = Turntables.tab()
        .row(anything).row(anything).row(anything).row(anything).row(anything)
        .row(anything).row(anything).row(anything).row(anything)
        .row(testInt(i -> i == 100));
    Tab actual = Turntables.tab()
        .row(100).row(2).row(2).row(2).row(2)
        .row(2).row(2).row(2).row(2).row(2);

    boolean res = matchInAnyOrder(expected, actual)
        .rowPermutationLimit(1L)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void assertionsOrderCanBlowUpPermutations_assumingActualSortedAsc_matches() {
    Predicate anything = test(o -> true);
    Tab expected = Turntables.tab()
        .row(anything).row(anything).row(anything).row(anything).row(anything)
        .row(anything).row(anything).row(anything).row(anything)
        .row(testInt(i -> i == 2));
    Tab actual = Turntables.tab()
        .row(2).row(2).row(2).row(2).row(2)
        .row(2).row(2).row(2).row(2).row(100);

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();
  }

  @Test
  public void assertionsBreadthCanBlowUpPermutations_assumingActualSortedDesc_matches() {
    Tab expected = Turntables.tab()
        .row(testInt(i -> i < 9)).row(testInt(i -> i < 9)).row(testInt(i -> i < 9))
        .row(testInt(i -> i < 9)).row(testInt(i -> i < 9)).row(testInt(i -> i < 9))
        .row(testInt(i -> i < 9)).row(testInt(i -> i < 9)).row(testInt(i -> i < 9))
        .row(testInt(i -> i > 0));
    Tab actual = Turntables.tab()
        .row(9).row(9).row(7).row(6).row(5)
        .row(4).row(3).row(2).row(1).row(0);

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void assertionsUniformityCanBlowUpPermutations_assumingActualSortedDesc_matches() {
    Tab expected = Turntables.tab()
        .row(testInt(i -> i < 9)).row(testInt(i -> i < 9)).row(testInt(i -> i < 9))
        .row(testInt(i -> i < 9)).row(testInt(i -> i < 9)).row(testInt(i -> i > 0))
        .row(testInt(i -> i > 0)).row(testInt(i -> i > 0)).row(testInt(i -> i > 0))
        .row(testInt(i -> i > 0));
    Tab actual = Turntables.tab()
        .row(9).row(9).row(9).row(9).row(9)
        .row(9).row(3).row(2).row(1).row(0);

    boolean res = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();
  }

  @Test
  public void actualRepresentation_whenAssertionRowsMismatch_valueRowsFollowTheExpectedOrder() {
    // matching assertion rows follow the expected order only if all assertion rows match

    Tab expected = Turntables.tab()
        .row(testInt(i -> i == 1), 2)
        .row(3, testInt(i -> i == 4)) // does not match
        .row(5, 6)
        .row(2, 20);

    Tab actual = Turntables.tab()
        .row(2, 20)
        .row(3, 5) // does not match
        .row(1, 2)
        .row(5, 6);

    AssertionProxy.Actual actualProxy = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy();
    boolean res = actualProxy.matchesExpected();
    assertThat(res).isFalse();

    String actualStringRepresentation = new StringBuilder()
        .append("Table:").append(LS)
        .append("    - col1 : 5").append(LS)
        .append("      col2 : 6").append(LS)
        .append("    - col1 : 2").append(LS)
        .append("      col2 : 20").append(LS)
        .append("    - col1 : 3").append(LS)
        .append("      col2 : 5").append(LS)
        .append("    - col1 : 1").append(LS)
        .append("      col2 : 2")
        .toString();

    assertThat(actualProxy.representation())
        .isEqualTo(actualStringRepresentation);
  }

  @Test
  public void actualRepresentation_whenValueRowsMismatch_matchingValueRowsFollowTheExpectedOrder() {
    // matching assertion rows follow the expected order only if all assertion rows match

    Tab expected = Turntables.tab()
        .row(testInt(i -> i == 1), 2)
        .row(3, testInt(i -> i == 4))
        .row(5, 6) // does not match
        .row(2, 20);

    Tab actual = Turntables.tab()
        .row(3, 4)
        .row(2, 20)
        .row(1, 2)
        .row(6, 6); // does not match

    AssertionProxy.Actual actualProxy = matchInAnyOrder(expected, actual)
        .buildOrGetActualProxy();
    boolean res = actualProxy.matchesExpected();
    assertThat(res).isFalse();

    String actualStringRepresentation = new StringBuilder()
        .append("Table:").append(LS)
        .append("    - col1 : 2").append(LS)
        .append("      col2 : 20").append(LS)
        .append("    - col1 : 3").append(LS)
        .append("      col2 : 4").append(LS)
        .append("    - col1 : 1").append(LS)
        .append("      col2 : 2").append(LS)
        .append("    - col1 : 6").append(LS)
        .append("      col2 : 6")
        .toString();

    assertThat(actualProxy.representation())
        .isEqualTo(actualStringRepresentation);
  }

  @Ignore // partial matching of assertion rows is not yet supported
  @Test
  public void expectedRepresentation_whenPartialMatch_containsActualValuesWhereMatched() {
    Tab expected = Turntables.tab()
        .row(testInt(i -> i == 1), 2)
        .row(3, testInt(i -> i == 4));

    Tab actual = Turntables.tab()
        .row(3, 5)
        .row(1, 2);

    AssertionProxy.Builder proxyBuilder = matchInAnyOrder(expected, actual);
    boolean res = proxyBuilder.buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isFalse();

    AssertionProxy.Expected expectedProxy = proxyBuilder.buildOrGetExpectedProxy();

    String expectedStringRepresentation = new StringBuilder()
        .append("Table:").append(LS)
        .append("    - col1 : 1").append(LS)
        .append("      col2 : 2").append(LS)
        .append("    - col1 : 3").append(LS)
        .append("      col2 : java.util.function.IntPredicate")
        .toString();

    assertThat(expectedProxy.representation())
        .isEqualTo(expectedStringRepresentation);
  }

  @Test
  public void expectedRepresentation_whenAllMatched_containsActualValues() {
    Tab expected = Turntables.tab()
        .row(testInt(i -> i == 1), 2)
        .row(3, testInt(i -> i == 4));

    Tab actual = Turntables.tab()
        .row(3, 4)
        .row(1, 2);

    AssertionProxy.Builder proxyBuilder = matchInAnyOrder(expected, actual);
    boolean res = proxyBuilder.buildOrGetActualProxy()
        .matchesExpected();
    assertThat(res).isTrue();

    AssertionProxy.Expected expectedProxy = proxyBuilder.buildOrGetExpectedProxy();

    String expectedStringRepresentation = new StringBuilder()
        .append("Table:").append(LS)
        .append("    - col1 : 1").append(LS)
        .append("      col2 : 2").append(LS)
        .append("    - col1 : 3").append(LS)
        .append("      col2 : 4")
        .toString();

    assertThat(expectedProxy.representation())
        .isEqualTo(expectedStringRepresentation);
  }
}
