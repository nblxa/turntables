package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;

public class ColOrderPrism extends AbstractTab {
  @NonNull
  private final List<String> colOrder;

  @NonNull
  static Tab ofActual(@NonNull Asserter asserter) {
    switch (asserter.getConf().colMode) {
      case MATCHES_IN_GIVEN_ORDER:
        return asserter.getConf().actual;
      case MATCHES_BY_NAME:
        List<String> expectedCols = Utils.stream(asserter.getConf().expected.cols())
            .map(Col::name)
            .collect(Collectors.toList());
        return new ColOrderPrism(asserter.getConf().actual, expectedCols);
      default:
        throw new UnsupportedOperationException();
    }
  }

  /**
   * Create a new Iterable of Cols with the values from <tt>cols</tt> but with the same order of
   * cols as in <tt>colOrder</tt>.
   *
   * Cols are matched by name.
   *
   * If the iterables don't fully match:
   * <ut>
   *   <li>if <tt>cols</tt> contains values that don't match any col name in <tt>colOrder</tt>,
   *   they will be placed at the end of the resulting iterable in the same order they were relative
   *   to each other;</li>
   *
   *   <li>if <tt>colOrder</tt> contains names that don't match any col names in <tt>cols</tt>,
   *   they will be ignored</li>
   *
   *   <li>if <tt>colOrder</tt> or <tt>cols</tt> contains duplicate names, they will be matched in
   *   the order of appearance.</li>
   * </ut>
   *
   * @param cols Cols to be re-ordered
   * @param colOrder the desired order of Col names
   * @return a new Iterable with re-ordered Cols
   */
  private static Iterable<Col> reorderCols(Iterable<Tab.Col> cols, List<String> colOrder) {
    List<Tab.Col> colsList = Utils.toArrayList(cols);
    List<Tab.Col> reorderedCols = new ArrayList<>(colsList.size());
    for (String orderedColName: colOrder) {
      Objects.requireNonNull(orderedColName, "orderedColName is null");
      ListIterator<Col> iter = colsList.listIterator();
      while (iter.hasNext()) {
        Tab.Col col = Objects.requireNonNull(iter.next(), "col is null");
        if (orderedColName.equals(col.name())) {
          iter.remove();
          reorderedCols.add(col);
          break;
        }
      }
    }
    reorderedCols.addAll(colsList);
    return reorderedCols;
  }

  private ColOrderPrism(@NonNull Tab tab, @NonNull List<String> colOrder) {
    super(reorderCols(tab.cols(), colOrder));
    this.colOrder = colOrder;
  }

  @NonNull
  @Override
  public Iterable<Row> rows() {
    return null;
  }

  private static class ReorderedRow extends AbstractRow {
    public ReorderedRow(Iterable<Col> cols, Iterable<Val> vals) {
      super(cols, vals);
    }

    @NonNull
    @Override
    public Iterable<Col> cols() {
      return super.cols();
    }

    @NonNull
    @Override
    public Iterable<Val> vals() {
      return super.vals();
    }
  }
}
