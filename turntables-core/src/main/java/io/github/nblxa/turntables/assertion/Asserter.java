package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Turntables;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Objects;

class Asserter {
  @NonNull
  private final AssertionProxy.Conf conf;
  @NonNull
  private final RowAsserter rowAsserter;
  @NonNull
  private final ColAsserter colAsserter;
  @NonNull
  private AssertionResult assertionResult;

  private Asserter(@NonNull AssertionProxy.Conf conf, @NonNull RowAsserter rowAsserter,
                   @NonNull ColAsserter colAsserter) {
    this.conf = conf;
    this.rowAsserter = rowAsserter;
    this.colAsserter = colAsserter;
    this.assertionResult = AssertionResult.NOT_YET_ASSERTED;
  }

  static Asserter createAsserter(@NonNull AssertionProxy.Conf conf) {
    Objects.requireNonNull(conf, "conf");
    ColAsserter colAsserter = createColAsserter(conf);
    ValAsserter valAsserter = createValAsserter(conf);
    RowAsserter rowAsserter = createRowAsserter(conf, valAsserter);
    return new Asserter(conf, rowAsserter, colAsserter);
  }

  private static RowAsserter createRowAsserter(@NonNull AssertionProxy.Conf conf,
                                               @NonNull ValAsserter valAsserter) {
    switch (conf.rowMode) {
      case MATCHES_IN_GIVEN_ORDER:
        return new OrderedRowAsserter(conf.expected.rows(), conf.actual.rows(),
            valAsserter);
      case MATCHES_IN_ANY_ORDER:
        return new UnorderedRowAsserter(conf.expected.rows(), conf.actual.rows(),
            conf.rowPermutationLimit, valAsserter);
      case MATCHES_BY_KEY:
        return new KeyBasedRowAsserter(conf.expected.rows(), conf.actual.rows(),
            conf.expected.cols(), conf.actual.cols(), conf.rowPermutationLimit,
            valAsserter);
      default:
        throw new UnsupportedOperationException();
    }
  }

  private static ColAsserter createColAsserter(@NonNull AssertionProxy.Conf conf) {
    final ColAsserter colAsserter;
    switch (conf.colMode) {
      case MATCHES_IN_GIVEN_ORDER:
        colAsserter = new OrderedColAsserter();
        break;
      case MATCHES_BY_NAME:
        colAsserter = new NamedColAsserter();
        break;
      default:
        throw new UnsupportedOperationException();
    }
    if (conf.rowMode == Turntables.RowMode.MATCHES_BY_KEY) {
      return new KeyColAsserter(colAsserter);
    } else {
      return colAsserter;
    }
  }

  private static ValAsserter createValAsserter(@NonNull AssertionProxy.Conf conf) {
    switch (conf.colMode) {
      case MATCHES_IN_GIVEN_ORDER:
        return new OrderedValAsserter();
      case MATCHES_BY_NAME:
        return new NamedValAsserter(conf.expected.cols(), conf.actual.cols());
      default:
        throw new UnsupportedOperationException();
    }
  }

  public boolean match() {
    AssertionResult.MatchResult colsMatch = AssertionResult.MatchResult.of(
        colAsserter.match(conf.expected.cols(), conf.actual.cols()));
    assertionResult = assertionResult.colsMatched(colsMatch);
    if (assertionResult.colsMatched() != AssertionResult.MatchResult.MATCH) {
      return false;
    }
    AssertionResult.MatchResult rowsMatch = AssertionResult.MatchResult.of(rowAsserter.match());
    assertionResult = assertionResult.rowsMatched(rowsMatch);
    return assertionResult.rowsMatched() == AssertionResult.MatchResult.MATCH
        && assertionResult.colsMatched() == AssertionResult.MatchResult.MATCH;
  }

  public AssertionResult getOrCalculateResult() {
    if (assertionResult == AssertionResult.NOT_YET_ASSERTED) {
      match();
    }
    return assertionResult;
  }

  @NonNull
  RowAsserter getRowAsserter() {
    return rowAsserter;
  }

  @NonNull
  ColAsserter getColAsserter() {
    return colAsserter;
  }

  @NonNull
  AssertionProxy.Conf getConf() {
    return conf;
  }

  @NonNull
  @Override
  public String toString() {
    return String.format("Asserter[conf=%s, rowAsserter=%s, colAsserter=%s]",
        conf, rowAsserter.getClass().getSimpleName(), colAsserter.getClass().getSimpleName());
  }
}
