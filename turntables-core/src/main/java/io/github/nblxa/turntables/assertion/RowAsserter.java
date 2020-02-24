package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RowAsserter {
  boolean match();

  @NonNull
  List<Map.Entry<Optional<Tab.Row>, Optional<Tab.Row>>> getRowPairs();
}
