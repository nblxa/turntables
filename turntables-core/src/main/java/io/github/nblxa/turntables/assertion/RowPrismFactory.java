package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class RowPrismFactory {
  @NonNull
  static Tab createFromExpected(@NonNull Asserter asserter, @NonNull Tab expected) {
    return of(asserter, expected, Map.Entry::getKey);
  }

  @NonNull
  static Tab createFromActual(@NonNull Asserter asserter, Tab actual) {
    return of(asserter, actual, Map.Entry::getValue);
  }

  @NonNull
  private static Tab of(@NonNull Asserter asserter, @NonNull Tab tab,
                @NonNull Function<? super Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>>,
                    ? extends Optional<Tab.Row>> rowFunction) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(tab, "tab is null");
    switch (asserter.getConf().rowMode) {
      case MATCHES_IN_ANY_ORDER:
      case MATCHES_BY_KEY:
        return new RowOrderPrism(asserter.getRowAsserter(), tab, rowFunction);
      case MATCHES_IN_GIVEN_ORDER:
        return new RowDiffPrism(asserter.getRowAsserter(), tab, rowFunction);
      default:
        throw new UnsupportedOperationException();
    }
  }

  private RowPrismFactory() {
  }
}
