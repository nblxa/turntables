package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.Utils;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;
import java.util.Objects;

class OrderedColAsserter implements ColAsserter {
  @Override
  public boolean match(@NonNull Iterable<Tab.Col> expected, @NonNull Iterable<Tab.Col> actual) {
    Objects.requireNonNull(expected, "expected");
    Objects.requireNonNull(actual, "actual");
    List<Tab.Col> expList = Utils.toArrayList(expected);
    List<Tab.Col> actList = Utils.toArrayList(actual);
    if (expList.size() != actList.size()) {
      return false;
    }
    for (int i = 0; i < expList.size(); i++) {
      Typ expTyp = expList.get(i).typ();
      Typ actTyp = actList.get(i).typ();
      if (!expTyp.equals(actTyp)) {
        return false;
      }
    }
    return true;
  }
}
