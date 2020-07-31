package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class RowPrismFactory {
  @NonNull
  static Prism createFromExpected(@NonNull Asserter asserter, @NonNull Tab expected) {
    return of(asserter, expected, Map.Entry::getKey);
  }

  @NonNull
  static Prism createFromActual(@NonNull Asserter asserter, Tab actual) {
    return of(asserter, actual, Map.Entry::getValue);
  }

  @NonNull
  private static Prism of(@NonNull Asserter asserter, @NonNull Tab tab,
                @NonNull Function<? super Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>>,
                    ? extends Optional<Tab.Row>> rowFunction) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(tab, "tab is null");
    switch (asserter.getConf().settings.rowMode) {
      case MATCH_IN_ANY_ORDER:
      case MATCH_BY_KEY:
        return new RowOrderPrism(asserter.getRowAsserter(), tab, rowFunction);
      case MATCH_IN_GIVEN_ORDER:
        return NoOpPrism.of(tab);
      default:
        throw new UnsupportedOperationException();
    }
  }

  private RowPrismFactory() {
  }
}
