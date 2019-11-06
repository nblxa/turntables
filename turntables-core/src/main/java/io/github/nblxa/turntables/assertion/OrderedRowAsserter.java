package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Utils;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

class OrderedRowAsserter extends AbstractRowAsserter {
  @NonNull
  final Iterable<Tab.Row> expected;
  @NonNull
  final Iterable<Tab.Row> actual;

  OrderedRowAsserter(@NonNull Iterable<Tab.Row> expected, @NonNull Iterable<Tab.Row> actual,
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
  public Iterable<Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>>> getRowPairs() {
    return Utils.paired(expected, actual, (e, a) -> Utils.entry(Optional.of(e), Optional.of(a)));
  }
}
