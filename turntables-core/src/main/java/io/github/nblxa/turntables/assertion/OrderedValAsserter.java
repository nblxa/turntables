package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Iterator;

class OrderedValAsserter extends AbstractMatchingValAsserter {
  @Override
  public boolean match(@NonNull Iterable<Tab.Val> expected, @NonNull Iterable<Tab.Val> actual) {
    Iterator<Tab.Val> expIter = expected.iterator();
    Iterator<Tab.Val> actIter = actual.iterator();
    while (expIter.hasNext() && actIter.hasNext()) {
      if (!matchVals(expIter.next(), actIter.next())) {
        return false;
      }
    }
    return !(expIter.hasNext() || actIter.hasNext());
  }
}
