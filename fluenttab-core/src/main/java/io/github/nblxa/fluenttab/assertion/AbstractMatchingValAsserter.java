package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractMatchingValAsserter implements ValAsserter {
  @Override
  public boolean matchVals(@NonNull Tab.Val expected, @NonNull Tab.Val actual) {
    return expected.matchesActual(actual);
  }
}
