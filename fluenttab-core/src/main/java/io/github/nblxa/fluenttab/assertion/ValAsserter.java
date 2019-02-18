package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface ValAsserter {
  boolean match(@NonNull Iterable<Tab.Val> expected, @NonNull Iterable<Tab.Val> actual);

  boolean matchVals(@NonNull Tab.Val expected, @NonNull Tab.Val actual);
}
