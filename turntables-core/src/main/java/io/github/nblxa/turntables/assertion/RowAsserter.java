package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Map;
import java.util.Optional;

public interface RowAsserter {
  boolean match();

  @NonNull
  Iterable<Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>>> getRowPairs();
}
