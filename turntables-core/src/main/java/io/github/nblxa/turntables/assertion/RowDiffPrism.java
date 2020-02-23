package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Utils;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RowDiffPrism extends AbstractTab {
  @NonNull
  private final RowAsserter rowAsserter;
  @NonNull
  private final Function<? super Map.Entry<Optional<Row>, Optional<Row>>,
      ? extends Optional<Row>> rowFunction;

  RowDiffPrism(@NonNull RowAsserter rowAsserter, @NonNull Tab tab,
               @NonNull Function<? super Map.Entry<Optional<Row>, Optional<Row>>,
                                 ? extends Optional<Row>> rowFunction) {
    super(tab.cols());
    this.rowAsserter = Objects.requireNonNull(rowAsserter, "rowAsserter is null");
    this.rowFunction = Objects.requireNonNull(rowFunction, "rowFunction is null");
  }

  @NonNull
  @Override
  public Iterable<Row> rows() {
    return Utils.stream(rowAsserter.getRowPairs())
        .map(rowFunction)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }
}
