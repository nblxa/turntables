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
  private final Function<? super Map.Entry<Optional<Row>, Optional<Row>>,
      ? extends Optional<Row>> rowFunction;

  private RowOrderPrism(@NonNull Asserter asserter, @NonNull Tab tab, @NonNull Function<? super Map.Entry<Optional<Row>,
      Optional<Row>>, ? extends Optional<Row>> rowFunction) {
    super(Objects.requireNonNull(tab, "tab is null").cols());
    Objects.requireNonNull(asserter, "asserter is null");
    this.rowAsserter = asserter.getRowAsserter();
    this.rowFunction = Objects.requireNonNull(rowFunction, "rowFunction is null");
  }

  static RowOrderPrism ofExpected(@NonNull Asserter asserter, Tab tab) {
    return new RowOrderPrism(asserter, tab, Map.Entry::getKey);
  }

  static RowOrderPrism ofActual(@NonNull Asserter asserter, Tab tab) {
    return new RowOrderPrism(asserter, tab, Map.Entry::getValue);
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
