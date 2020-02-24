package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Typ;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;
import java.util.Objects;

class OrderedColAsserter implements ColAsserter {
  @Override
  public boolean match(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual) {
    Objects.requireNonNull(expected, "expected");
    Objects.requireNonNull(actual, "actual");
    if (expected.size() != actual.size()) {
      return false;
    }
    for (int i = 0; i < expected.size(); i++) {
      Typ expTyp = expected.get(i).typ();
      Typ actTyp = actual.get(i).typ();
      if (!expTyp.equals(actTyp)) {
        return false;
      }
    }
    return true;
  }
}
