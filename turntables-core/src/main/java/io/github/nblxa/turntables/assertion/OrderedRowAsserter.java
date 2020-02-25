package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Utils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class OrderedRowAsserter extends AbstractRowAsserter {
  @NonNull
  final List<Tab.Row> expected;
  @NonNull
  final List<Tab.Row> actual;

  OrderedRowAsserter(@NonNull List<Tab.Row> expected, @NonNull List<Tab.Row> actual,
                     @NonNull ValAsserter valAsserter) {
    super(valAsserter);
    this.expected = expected;
    this.actual = actual;
  }

  @Override
  public boolean match() {
    Iterator<Tab.Row> expIter = expected.iterator();
    Iterator<Tab.Row> actIter = actual.iterator();
    while (expIter.hasNext() && actIter.hasNext()) {
      if (!matchRows(expIter.next(), actIter.next())) {
        return false;
      }
    }
    return !(expIter.hasNext() || actIter.hasNext());
  }

  @NonNull
  @Override
  public List<Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>>> getRowPairs() {
    return Utils.pairedSparsely(expected, actual, Utils::entry);
  }
}
