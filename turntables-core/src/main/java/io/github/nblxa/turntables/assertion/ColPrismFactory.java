package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import java.util.Objects;

public class ColPrismFactory {
  @NonNull
  static Prism createFromExpected(@NonNull Asserter asserter, @NonNull Tab expected, @NonNull Tab actual) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(expected, "expected is null");
    Objects.requireNonNull(actual, "actual is null");
    switch (asserter.getConf().colMode) {
      case MATCHES_IN_GIVEN_ORDER:
        return ColNamePrism.ofExpected(asserter, expected, actual);
      case MATCHES_BY_NAME:
        return NoOpPrism.of(expected);
      default:
        throw new UnsupportedOperationException();
    }
  }

  @NonNull
  static Prism createFromActual(@NonNull Asserter asserter, @NonNull Tab expected, @NonNull Tab actual) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(expected, "expected is null");
    Objects.requireNonNull(actual, "actual is null");
    Prism res;
    switch (asserter.getConf().colMode) {
      case MATCHES_IN_GIVEN_ORDER:
        res = ColNamePrism.ofActual(asserter, expected, actual);
        break;
      case MATCHES_BY_NAME:
        res = ColOrderPrism.ofActual(asserter, expected, actual);
        if (asserter.getConf().settings.colNamesMode == Settings.ColNamesMode.CASE_INSENSITIVE) {
          res = ColNamePrism.ofActual(asserter, expected, res);
        }
        break;
      default:
        throw new UnsupportedOperationException();
    }
    return res;
  }

  private ColPrismFactory() {
  }
}
