package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Utils;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class RowOrderPrism extends AbstractTab {
  @NonNull
  private final RowAsserter rowAsserter;
  @NonNull
  private final Function<? super Map.Entry<Optional<Row>, Optional<Row>>,
      ? extends Optional<Row>> rowFunction;

  @NonNull
  static Tab ofExpected(@NonNull Asserter asserter, @NonNull Tab expected) {
    return of(asserter, expected, Map.Entry::getKey);
  }

  @NonNull
  static Tab ofActual(@NonNull Asserter asserter, Tab actual) {
    return of(asserter, actual, Map.Entry::getValue);
  }

  @NonNull
  private static Tab of(@NonNull Asserter asserter, @NonNull Tab tab,
                        @NonNull Function<? super Map.Entry<Optional<Row>, Optional<Row>>,
                            ? extends Optional<Row>> rowFunction) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(tab, "tab is null");
    switch (asserter.getConf().rowMode) {
      case MATCHES_IN_ANY_ORDER:
      case MATCHES_BY_KEY:
        return new RowOrderPrism(asserter.getRowAsserter(), tab, rowFunction);
      case MATCHES_IN_GIVEN_ORDER:
        return tab;
      default:
        throw new UnsupportedOperationException();
    }
  }

  private RowOrderPrism(@NonNull RowAsserter rowAsserter, @NonNull Tab tab,
                        @NonNull Function<? super Map.Entry<Optional<Row>, Optional<Row>>,
                                          ? extends Optional<Row>> rowFunction) {
    super(tab.cols());
    this.rowAsserter = Objects.requireNonNull(rowAsserter, "rowAsserter is null");
    this.rowFunction = Objects.requireNonNull(rowFunction, "rowFunction is null");
  }

  @NonNull
  @Override
  public Iterable<Row> rows() {
    return Utils.asList(rowAsserter.getRowPairs()).stream()
        .map(rowFunction)
        .flatMap(o -> o.map(Stream::of).orElse(Stream.empty()))
        .collect(Collectors.toList());
  }
}
