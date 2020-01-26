package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.Utils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ColNamePrism extends AbstractTab {
  @NonNull
  private final Tab tab;

  @NonNull
  static Tab ofActual(@NonNull Asserter asserter, @NonNull Tab expected, @NonNull Tab actual) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(actual, "actual is null");
    switch (asserter.getConf().colMode) {
      case MATCHES_IN_GIVEN_ORDER:
        if (!TableUtils.hasNamedCols(expected) && TableUtils.hasNamedCols(actual)) {
          return actual;
        }
        List<String> expectedCols = Utils.stream(expected.cols())
            .map(Col::name)
            .collect(Collectors.toList());
        return new ColNamePrism(actual, expectedCols);
      case MATCHES_BY_NAME:
        return actual;
      default:
        throw new UnsupportedOperationException();
    }
  }

  @NonNull
  static Tab ofExpected(@NonNull Asserter asserter, @NonNull Tab expected, @NonNull Tab actual) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(expected, "expected is null");
    Objects.requireNonNull(actual, "actual is null");
    switch (asserter.getConf().colMode) {
      case MATCHES_IN_GIVEN_ORDER:
        if (TableUtils.hasNamedCols(expected)) {
          return expected;
        }
        List<String> actualCols = Utils.stream(actual.cols())
            .map(Col::name)
            .collect(Collectors.toList());
        return new ColNamePrism(expected, actualCols);
      case MATCHES_BY_NAME:
        return expected;
      default:
        throw new UnsupportedOperationException();
    }
  }

  private static Iterable<Tab.Col> renameFirstCols(Iterable<Tab.Col> cols, List<String> colNames) {
    List<Tab.Col> colsList = Utils.toArrayList(cols);
    for (int i = 0; i < Math.min(colsList.size(), colNames.size()); i++) {
      Tab.Col oldCol = colsList.get(i);
      colsList.set(i, new RenamedCol(oldCol, colNames.get(i)));
    }
    return colsList;
  }

  private static class RenamedCol implements Tab.Col {
    private final Tab.Col col;
    private final String newName;

    public RenamedCol(Col col, String newName) {
      this.col = col;
      this.newName = newName;
    }

    @NonNull
    @Override
    public String name() {
      return newName;
    }

    @NonNull
    @Override
    public Typ typ() {
      return col.typ();
    }

    @NonNull
    @Override
    public Val valOf(@Nullable Object o) {
      return col.valOf(o);
    }

    @Override
    public boolean accepts(@NonNull Val val) {
      return col.accepts(val);
    }

    @Override
    public boolean isKey() {
      return col.isKey();
    }
  }

  private ColNamePrism(@NonNull Tab tab, @NonNull List<String> colNames) {
    super(renameFirstCols(tab.cols(), colNames));
    this.tab = tab;
  }

  @NonNull
  @Override
  public Iterable<Row> rows() {
    return Utils.stream(tab.rows())
        .map(r -> new RenamedColRow(r, cols()))
        .collect(Collectors.toList());
  }

  private static class RenamedColRow implements Tab.Row {
    private final Tab.Row row;
    private final Iterable<Tab.Col> cols;

    public RenamedColRow(Row row, Iterable<Tab.Col> renamedCols) {
      this.row = row;
      this.cols = renamedCols;
    }

    @NonNull
    @Override
    public Iterable<Col> cols() {
      return cols;
    }

    @NonNull
    @Override
    public Iterable<Val> vals() {
      return row.vals();
    }
  }
}
