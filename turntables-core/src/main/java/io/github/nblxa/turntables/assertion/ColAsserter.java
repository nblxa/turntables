package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface ColAsserter {
  boolean match(@NonNull Iterable<Tab.Col> expected, @NonNull Iterable<Tab.Col> actual);
}
