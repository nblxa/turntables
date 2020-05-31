package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ColNamePrism extends Prism implements Tab {
  @NonNull
  private final Tab tab;

  @NonNull
  static Prism ofActual(@NonNull Asserter asserter, @NonNull Tab expected,
                                  @NonNull Tab actual) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(expected, "expected is null");
    Objects.requireNonNull(actual, "actual is null");
    if (asserter.getConf().colMode != Turntables.ColMode.MATCHES_IN_GIVEN_ORDER) {
      throw new UnsupportedOperationException();
    }
    if (!TableUtils.hasNamedCols(expected) && TableUtils.hasNamedCols(actual)) {
      return NoOpPrism.of(actual);
    }
    List<String> expectedCols = TableUtils.colNames(expected);
    List<Tab.Col> renamedCols = renameFirstCols(actual, expectedCols);
    return new ColNamePrism(actual, renamedCols);
  }

  @NonNull
  static Prism ofExpected(@NonNull Asserter asserter, @NonNull Tab expected, @NonNull Tab actual) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(expected, "expected is null");
    Objects.requireNonNull(actual, "actual is null");
    switch (asserter.getConf().colMode) {
      case MATCHES_IN_GIVEN_ORDER:
        if (TableUtils.hasNamedCols(expected)) {
          return NoOpPrism.of(expected);
        }
        List<String> actualColNames = TableUtils.colNames(actual);
        List<Tab.Col> renamedCols = renameFirstCols(expected, actualColNames);
        return new ColNamePrism(expected, renamedCols);
      case MATCHES_BY_NAME:
        return NoOpPrism.of(expected);
      default:
        throw new UnsupportedOperationException();
    }
  }

  @NonNull
  private static List<Tab.Col> renameFirstCols(@NonNull Tab tab,
                                                    @NonNull List<String> colNames) {
    List<Tab.Col> cols = new ArrayList<>(tab.cols());
    for (int i = 0; i < Math.min(cols.size(), colNames.size()); i++) {
      Tab.Col oldCol = cols.get(i);
      cols.set(i, new RenamedCol(oldCol, colNames.get(i)));
    }
    return cols;
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

    @Override
    public boolean isKey() {
      return col.isKey();
    }
  }

  private ColNamePrism(@NonNull Tab tab, @NonNull List<Tab.Col> renamedCols) {
    super(renamedCols);
    this.tab = tab;
  }

  @NonNull
  @Override
  public List<Row> rows() {
    return tab.rows()
        .stream()
        .map(r -> new RenamedColRow(r, cols()))
        .collect(Collectors.toList());
  }

  private static class RenamedColRow extends Prism.PrismRow {
    private final Tab.Row row;

    public RenamedColRow(Row row, List<Tab.Col> renamedCols) {
      super(renamedCols);
      this.row = row;
    }

    @NonNull
    @Override
    public List<Val> vals() {
      return row.vals();
    }
  }
}
