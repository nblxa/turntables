package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Turntables;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class NamedValAsserter extends AbstractMatchingValAsserter {
  private final List<Integer> actIndexes;

  NamedValAsserter(@NonNull List<Tab.Col> expCols, @NonNull List<Tab.Col> actCols) {
    List<String> expNames = names(expCols);
    List<String> actNames = names(actCols);
    actIndexes = new ArrayList<>(actNames.size());
    for (String expName : expNames) {
      int actIndex = indexOf(actNames, expName);
      if (actIndex != -1) {
        actIndexes.add(actIndex);
      }
    }
  }

  @Override
  public boolean match(@NonNull List<Tab.Val> expected, @NonNull List<Tab.Val> actual) {
    for (int i = 0; i < expected.size(); i++) {
      Tab.Val expVal = expected.get(i);
      int actIndex = actIndexes.get(i);
      Tab.Val actVal = actual.get(actIndex);
      if (!matchVals(expVal, actVal)) {
        return false;
      }
    }
    return true;
  }

  @NonNull
  private static List<String> names(@NonNull List<Tab.Col> cols) {
    return cols.stream()
        .map(Tab.Col::name)
        .collect(Collectors.toList());
  }

  private static int indexOf(List<String> actNames, String expName) {
    if (Turntables.getSettings().nameMode == Settings.NameMode.CASE_INSENSITIVE) {
      for (int i = 0; i < actNames.size(); i++) {
        if (actNames.get(i).equalsIgnoreCase(expName)) {
          return i;
        }
      }
      return -1;
    } else {
      return actNames.indexOf(expName);
    }
  }
}
