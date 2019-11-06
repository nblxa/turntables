package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Utils;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;
import java.util.stream.Collectors;

class KeyColAsserter implements ColAsserter {
  private final ColAsserter colAsserter;

  KeyColAsserter(ColAsserter colAsserter) {
    this.colAsserter = colAsserter;
  }

  private static List<Tab.Col> keyCols(Iterable<Tab.Col> cols) {
    return Utils.stream(cols)
        .filter(Tab.Col::isKey)
        .collect(Collectors.toList());
  }

  @Override
  public boolean match(@NonNull Iterable<Tab.Col> expected, @NonNull Iterable<Tab.Col> actual) {
    List<Tab.Col> expKeyCols = keyCols(expected);
    List<Tab.Col> actKeyCols = keyCols(actual);
    return colAsserter.match(expKeyCols, actKeyCols) && colAsserter.match(expected, actual);
  }
}
