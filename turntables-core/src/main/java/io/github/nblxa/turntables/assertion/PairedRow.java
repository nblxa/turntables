package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Utils;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

class PairedRow<E extends Tab.Val> extends AbstractTab.AbstractRow {
  @NonNull
  private final List<Tab.Val> vals;

  private PairedRow(@NonNull Tab.Row expected, @NonNull Tab.Row actual,
                    @NonNull BiFunction<Tab.Val, Tab.Val, E> valConstr) {
    super(expected.cols(), expected.vals());
    this.vals = new ArrayList<>();
    Utils.paired(expected.vals(), actual.vals(), valConstr)
        .forEach(vals::add);
  }

  @NonNull
  static <E extends Tab.Val> BiFunction<Tab.Row, Tab.Row, PairedRow<E>> of(
      @NonNull BiFunction<Tab.Val, Tab.Val, E> valConstr) {
    return (exp, act) -> new PairedRow<>(exp, act, valConstr);
  }

  @NonNull
  @Override
  public Iterable<Tab.Val> vals() {
    return vals;
  }
}
