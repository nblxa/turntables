package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.Utils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class AssertionValPrism extends AbstractTab {
  private final List<Row> augmentedRows;

  static Tab ofExpected(@NonNull Tab expected, @NonNull Tab actual) {
    return new AssertionValPrism(expected, actual);
  }

  private AssertionValPrism(@NonNull Tab expected, @NonNull Tab actual) {
    super(expected.cols());
    Objects.requireNonNull(actual, "actual");
    this.augmentedRows = new ArrayList<>();
    Utils.paired(expected.rows(), actual.rows(), PairedRow.of(EvalToActualIfMatches::new))
        .forEach(augmentedRows::add);
  }

  @NonNull
  @Override
  public Iterable<Row> rows() {
    return augmentedRows;
  }

  static final class EvalToActualIfMatches extends AbstractVal {
    private final Val expected;
    private final Val actual;
    private final boolean matches;

    EvalToActualIfMatches(Val expected, Val actual) {
      this.expected = expected;
      this.actual = actual;
      this.matches = expected.matchesActual(actual);
    }

    @NonNull
    @Override
    public Typ getTyp() {
      return expected.getTyp();
    }

    @Nullable
    @Override
    public Object eval() {
      if (matches) {
        return actual.eval();
      } else {
        return expected.eval();
      }
    }

    @Override
    public boolean matchesActual(@NonNull Val other) {
      return expected.matchesActual(other);
    }

    @Override
    public String toString() {
      if (matches) {
        return actual.toString();
      } else {
        return expected.toString();
      }
    }
  }
}
