package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.Utils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

class AssertionValPrism extends AbstractTab {
  private final List<Row> augmentedRows;

  static Tab ofExpected(@NonNull Tab expected, @NonNull Tab actual) {
    return new AssertionValPrism(expected, actual);
  }

  private AssertionValPrism(@NonNull Tab expected, @NonNull Tab actual) {
    super(expected.cols());
    Objects.requireNonNull(actual, "actual");
    this.augmentedRows = new ArrayList<>();
    Utils.pairedSparsely(expected.rows(), actual.rows(), (e, a) -> augmentedRow(Utils.entry(e, a)))
        .forEach(o -> o.ifPresent(augmentedRows::add));
  }

  private static Optional<Tab.Row> augmentedRow(Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>> e) {
    return e.getKey().map(exp -> {
      Optional<Tab.Row> optAct = e.getValue();
      List<Col> cols = Utils.toArrayList(exp.cols());
      return new AbstractTab.AbstractRow(cols) {
        @NonNull
        @Override
        public Iterable<Val> vals() {
          Iterable<Val> vals = optAct.map(act ->
              Utils.<Val, Val>pairedSparsely(exp.vals(), act.vals(), EvalToActualIfMatches::new))
                .orElse(exp.vals());
          // Only include values that can be mapped to existing columns
          return Utils.toArrayList(vals)
              .subList(0, cols.size());
        }
      };
    });
  }

  @NonNull
  @Override
  public Iterable<Row> rows() {
    return augmentedRows;
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  static final class EvalToActualIfMatches extends AbstractVal {
    private final Optional<Tab.Val> expected;
    private final Optional<Tab.Val> actual;
    private final boolean matches;

    EvalToActualIfMatches(Optional<Tab.Val> expected, Optional<Tab.Val> actual) {
      this.expected = expected;
      this.actual = actual;
      this.matches = expected.flatMap(exp -> actual.map(exp::matchesActual))
          .orElse(false);
    }

    @NonNull
    @Override
    public Typ getTyp() {
      return apply(Val::getTyp);
    }

    @Nullable
    @Override
    public Object eval() {
      return apply(Val::eval);
    }

    @Override
    public boolean matchesActual(@NonNull Val actual) {
      return expected.map(exp -> exp.matchesActual(actual))
          .orElse(false);
    }

    @Override
    public String toString() {
      return apply(Object::toString);
    }

    private <T> T apply(Function<Val, T> func) {
      if (matches) {
        // if matches, actual is always present
        // noinspection OptionalGetWithoutIsPresent
        return func.apply(actual.get());
      } else {
        // one of actual and expected is always present
        // noinspection OptionalGetWithoutIsPresent
        return expected.map(func)
            .orElseGet(() -> func.apply(actual.get()));
      }
    }
  }
}
