package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;
import java.util.stream.Collectors;

class KeyColAsserter implements ColAsserter {
  private final ColAsserter colAsserter;

  KeyColAsserter(ColAsserter colAsserter) {
    this.colAsserter = colAsserter;
  }

  private static List<Tab.Col> keyCols(List<Tab.Col> cols) {
    return cols.stream()
        .filter(Tab.Col::isKey)
        .collect(Collectors.toList());
  }

  @Override
  public boolean match(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual) {
    List<Tab.Col> expKeyCols = keyCols(expected);
    List<Tab.Col> actKeyCols = keyCols(actual);
    return colAsserter.match(expKeyCols, actKeyCols) && colAsserter.match(expected, actual);
  }
}
