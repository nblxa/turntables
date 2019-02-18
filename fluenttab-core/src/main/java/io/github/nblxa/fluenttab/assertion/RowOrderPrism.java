package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.AbstractTab;
import io.github.nblxa.fluenttab.Utils;
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
  private final boolean isExpected;

  private RowOrderPrism(@NonNull Asserter asserter, boolean isExpected) {
    super(asserter.getConf().actual.cols());
    Objects.requireNonNull(asserter, "asserter");
    this.rowAsserter = asserter.getRowAsserter();
    this.isExpected = isExpected;
  }

  static RowOrderPrism ofExpected(@NonNull Asserter asserter) {
    return new RowOrderPrism(asserter, true);
  }

  static RowOrderPrism ofActual(@NonNull Asserter asserter) {
    return new RowOrderPrism(asserter, false);
  }

  @NonNull
  @Override
  public Iterable<Row> rows() {
    return Utils.asList(rowAsserter.getRowPairs()).stream()
        .map(rowFunction())
        .flatMap(o -> o.map(Stream::of).orElse(Stream.empty()))
        .collect(Collectors.toList());
  }

  private Function<? super Map.Entry<Optional<Row>, Optional<Row>>,
      ? extends Optional<Row>> rowFunction() {
    if (isExpected) {
      return Map.Entry::getKey;
    } else {
      return Map.Entry::getValue;
    }
  }
}
