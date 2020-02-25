package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;

public interface ValAsserter {
  boolean match(@NonNull List<Tab.Val> expected, @NonNull List<Tab.Val> actual);

  boolean matchVals(@NonNull Tab.Val expected, @NonNull Tab.Val actual);
}
