package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class NamedValAsserter extends AbstractMatchingValAsserter {
  private final List<Integer> actIndexes;

  NamedValAsserter(@NonNull List<Tab.Col> expCols, @NonNull List<Tab.Col> actCols) {
    List<String> expNames = names(expCols);
    List<String> actNames = names(actCols);
    actIndexes = new ArrayList<>(actNames.size());
    for (String expName : expNames) {
      int actIndex = actNames.indexOf(expName);
      if (actIndex != -1) {
        actIndexes.add(actIndex);
      }
    }
  }

  @NonNull
  private static List<String> names(@NonNull List<Tab.Col> cols) {
    List<String> names = new ArrayList<>();
    Iterator<Tab.Col> iter = cols.iterator();
    while (iter.hasNext()) {
      Tab.Col c = iter.next();
      if (c instanceof Tab.NamedCol) {
        names.add(((Tab.NamedCol) c).name());
      } else {
        throw new IllegalArgumentException("Cannot match unnamed cols by name!");
      }
    }
    return names;
  }

  @Override
  public boolean match(@NonNull List<Tab.Val> expected, @NonNull List<Tab.Val> actual) {
    Iterator<Tab.Val> expIter = expected.iterator();
    int i = 0;
    while (expIter.hasNext()) {
      int actIndex = actIndexes.get(i);
      Tab.Val actVal = actual.get(actIndex);
      if (!matchVals(expIter.next(), actVal)) {
        return false;
      }
      i++;
    }
    return true;
  }
}
