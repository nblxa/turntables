package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;
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

  RowOrderPrism(@NonNull RowAsserter rowAsserter, @NonNull Tab tab,
                @NonNull Function<? super Map.Entry<Optional<Row>, Optional<Row>>,
                                  ? extends Optional<Row>> rowFunction) {
    super(tab.cols());
    this.rowAsserter = Objects.requireNonNull(rowAsserter, "rowAsserter is null");
    this.rowFunction = Objects.requireNonNull(rowFunction, "rowFunction is null");
  }

  @NonNull
  @Override
  public List<Row> rows() {
    return rowAsserter.getRowPairs()
        .stream()
        .map(rowFunction)
        .flatMap(o -> o.map(Stream::of).orElse(Stream.empty()))
        .collect(Collectors.toList());
  }
}
