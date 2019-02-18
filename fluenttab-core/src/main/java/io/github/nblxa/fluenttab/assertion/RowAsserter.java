package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Map;
import java.util.Optional;

public interface RowAsserter {
  boolean match();

  @NonNull
  Iterable<Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>>> getRowPairs();
}
