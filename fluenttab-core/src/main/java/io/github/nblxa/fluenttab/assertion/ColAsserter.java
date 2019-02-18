package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface ColAsserter {
  boolean match(@NonNull Iterable<Tab.Col> expected, @NonNull Iterable<Tab.Col> actual);
}
