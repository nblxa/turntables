package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface ValAsserter {
  boolean match(@NonNull Iterable<Tab.Val> expected, @NonNull Iterable<Tab.Val> actual);

  boolean matchVals(@NonNull Tab.Val expected, @NonNull Tab.Val actual);
}
