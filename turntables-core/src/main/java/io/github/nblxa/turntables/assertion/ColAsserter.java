package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;

public interface ColAsserter {
  boolean match(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual);
}
