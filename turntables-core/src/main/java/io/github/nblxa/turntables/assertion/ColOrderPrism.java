package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.TableUtils;
import io.github.nblxa.turntables.Turntables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ColOrderPrism extends Prism {
  @NonNull
  private final List<Tab.Row> rows;

  @NonNull
  static Prism ofActual(@NonNull Asserter asserter, @NonNull Tab expected, @NonNull Tab actual) {
    Objects.requireNonNull(asserter, "asserter is null");
    Objects.requireNonNull(expected, "expected is null");
    Objects.requireNonNull(actual, "actual is null");
    if (asserter.getConf().colMode != Turntables.ColMode.MATCHES_BY_NAME) {
      throw new UnsupportedOperationException();
    }
    List<String> expectedColNames = TableUtils.colNames(expected);
    List<Integer> reorderSeq = reorderSequence(actual.cols(), expectedColNames);
    return new ColOrderPrism(actual, reorderSeq);
  }

  @NonNull
  private static List<Integer> reorderSequence(@NonNull List<Tab.Col> originalCols,
                                               @NonNull List<String> newNames) {
    Map<Integer, Col> colMap = new LinkedHashMap<>();
    for (int i = 0; i < originalCols.size(); i++) {
      colMap.put(i, originalCols.get(i));
    }
    List<Integer> result = new ArrayList<>();
    for (String newName: newNames) {
      Objects.requireNonNull(newName, "newName is null");
      for (Iterator<Map.Entry<Integer, Tab.Col>> it = colMap.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry<Integer, Tab.Col> entry = it.next();
        Tab.Col col = entry.getValue();
        String name = Objects.requireNonNull(col.name(), "name is required");
        if (name.equals(newName)) {
          result.add(entry.getKey());
          it.remove();
          break;
        }
      }
    }
    result.addAll(colMap.keySet());
    assert result.size() == originalCols.size();
    return Collections.unmodifiableList(result);
  }

  @NonNull
  private static <T> List<T> reorderItems(@NonNull List<T> originalItems,
                                          @NonNull List<Integer> reorderSeq) {
    List<T> result = new ArrayList<>();
    for (int i : reorderSeq) {
      result.add(originalItems.get(i));
    }
    return Collections.unmodifiableList(result);
  }

  ColOrderPrism(@NonNull Tab tab, @NonNull List<Integer> reorderSeq) {
    super(reorderItems(tab.cols(), reorderSeq));
    this.rows = rowsWithReorderedCols(tab.rows(), reorderSeq);
  }

  @NonNull
  private List<Tab.Row> rowsWithReorderedCols(@NonNull List<Tab.Row> originalRows,
                                              @NonNull List<Integer> reorderSeq) {
    List<Tab.Row> result = new ArrayList<>();
    for (Tab.Row originalRow: originalRows) {
      result.add(new RowWithReorderedItems(originalRow, reorderSeq));
    }
    return Collections.unmodifiableList(result);
  }

  @NonNull
  @Override
  public List<Row> rows() {
    return rows;
  }

  static final class RowWithReorderedItems extends Prism.PrismRow {
    @NonNull
    private final List<Val> vals;

    public RowWithReorderedItems(@NonNull Tab.Row originalRow, @NonNull List<Integer> reorderSeq) {
      super(reorderItems(originalRow.cols(), reorderSeq));
      this.vals = reorderItems(originalRow.vals(), reorderSeq);
    }

    @NonNull
    @Override
    public List<Val> vals() {
      return vals;
    }
  }
}
