package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Typ;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ColNamePrism extends AbstractTab implements Tab.NamedColTab {
  @NonNull
  private final Tab tab;
  @NonNull
  private final List<Tab.NamedCol> namedCols;

  @NonNull
  static Tab.NamedColTab ofActual(@NonNull Asserter asserter, @NonNull Tab expected,
                                  @NonNull Tab actual) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(actual, "actual is null");
    switch (asserter.getConf().colMode) {
      case MATCHES_IN_GIVEN_ORDER:
        if (!TableUtils.hasNamedCols(expected) && TableUtils.hasNamedCols(actual)) {
          return TableUtils.wrapWithNamedCols(actual);
        }
        List<String> expectedCols = TableUtils.colNames(expected);
        List<Tab.NamedCol> renamedCols = renameFirstCols(actual, expectedCols);
        return new ColNamePrism(actual, renamedCols);
      case MATCHES_BY_NAME:
        return TableUtils.wrapWithNamedCols(actual);
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
        List<String> actualCols = TableUtils.colNames(actual);
        List<Tab.NamedCol> renamedCols = renameFirstCols(expected, actualCols);
        return new ColNamePrism(expected, renamedCols);
      case MATCHES_BY_NAME:
        return expected;
      default:
        throw new UnsupportedOperationException();
    }
  }

  @NonNull
  private static List<Tab.NamedCol> renameFirstCols(@NonNull Tab tab,
                                                    @NonNull List<String> colNames) {
    List<Tab.NamedCol> cols = new ArrayList<>(TableUtils.wrapWithNamedCols(tab).namedCols());
    for (int i = 0; i < Math.min(cols.size(), colNames.size()); i++) {
      Tab.Col oldCol = cols.get(i);
      cols.set(i, new RenamedCol(oldCol, colNames.get(i)));
    }
    return cols;
  }

  private static class RenamedCol implements Tab.NamedCol {
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

  private ColNamePrism(@NonNull Tab tab, @NonNull List<Tab.NamedCol> renamedCols) {
    super(new ArrayList<>(renamedCols));
    this.tab = tab;
    this.namedCols = Collections.unmodifiableList(renamedCols);
  }

  @NonNull
  @Override
  public List<Row> rows() {
    return tab.rows()
        .stream()
        .map(r -> new RenamedColRow(r, cols()))
        .collect(Collectors.toList());
  }

  @NonNull
  @Override
  public List<NamedCol> namedCols() {
    return namedCols;
  }

  private static class RenamedColRow implements Tab.Row {
    private final Tab.Row row;
    private final List<Tab.Col> cols;

    public RenamedColRow(Row row, List<Tab.Col> renamedCols) {
      this.row = row;
      this.cols = renamedCols;
    }

    @NonNull
    @Override
    public List<Col> cols() {
      return cols;
    }

    @NonNull
    @Override
    public List<Val> vals() {
      return row.vals();
    }
  }
}
