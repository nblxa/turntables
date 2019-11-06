package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Utils;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

class NamedValAsserter extends AbstractMatchingValAsserter {
  private final List<Integer> actIndexes;

  NamedValAsserter(@NonNull Iterable<Tab.Col> expCols, @NonNull Iterable<Tab.Col> actCols) {
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

  private static List<String> names(Iterable<Tab.Col> cols) {
    return Utils.stream(cols)
        .map(c -> ((Tab.Named) c).name())
        .collect(Collectors.toList());
  }

  @Override
  public boolean match(@NonNull Iterable<Tab.Val> expected, @NonNull Iterable<Tab.Val> actual) {
    Iterator<Tab.Val> expIter = expected.iterator();
    List<Tab.Val> actList = Utils.asList(actual);
    int i = 0;
    while (expIter.hasNext()) {
      int actIndex = actIndexes.get(i);
      Tab.Val actVal = actList.get(actIndex);
      if (!matchVals(expIter.next(), actVal)) {
        return false;
      }
      i++;
    }
    return true;
  }
}
