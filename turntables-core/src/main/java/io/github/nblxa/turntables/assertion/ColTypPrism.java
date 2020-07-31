package io.github.nblxa.turntables.assertion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;

public class ColTypPrism extends Prism {
  @NonNull
  private final Tab tab;

  @NonNull
  static Prism ofActualPrism(@NonNull Asserter asserter, @NonNull Tab expected, @NonNull Tab actual) {
    if (asserter.getOrCalculateResult().colsMatched() != AssertionResult.MatchResult.MATCH
        && asserter.getConf().settings.decimalMode == Settings.DecimalMode.CONVERT) {
      List<Tab.Col> updatedCols = createUpdatedCols(asserter.getColAsserter(), expected.cols(),
          actual.cols());
      return new ColTypPrism(actual, updatedCols);
    } else {
      return NoOpPrism.of(actual);
    }
  }

  @NonNull
  private static List<Tab.Col> createUpdatedCols(@NonNull ColAsserter colAsserter,
                                                 @NonNull List<Tab.Col> expCols,
                                                 @NonNull List<Tab.Col> actCols) {
    List<Tab.Col> updatedCols = new ArrayList<>();
    Iterator<Tab.Col> expIter = expCols.iterator();
    Iterator<Tab.Col> actIter = actCols.iterator();
    while (expIter.hasNext() && actIter.hasNext()) {
      Tab.Col exp = expIter.next();
      Tab.Col act = actIter.next();
      if (colAsserter.matchCols(exp, act) && !exp.equals(act)) {
        updatedCols.add(exp);
      } else {
        updatedCols.add(act);
      }
    }
    while (actIter.hasNext()) {
      updatedCols.add(actIter.next());
    }
    return updatedCols;
  }

  private ColTypPrism(@NonNull Tab tab, @NonNull List<Col> updatedCols) {
    super(updatedCols);
    this.tab = tab;
  }

  @NonNull
  @Override
  public List<Row> rows() {
    return tab.rows();
  }
}
