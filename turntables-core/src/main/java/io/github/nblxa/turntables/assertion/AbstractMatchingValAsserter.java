package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractMatchingValAsserter implements ValAsserter {
  @Override
  public boolean matchVals(@NonNull Tab.Val expected, @NonNull Tab.Val actual) {
    return expected.matchesActual(actual);
  }
}
