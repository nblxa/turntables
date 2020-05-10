package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;

public interface ColAsserter {

  /**
   * Check that the columns match. The exact definition of the match depends on the implementation.
   *
   * @param expected expected list of columns
   * @param actual actual list of columns
   * @return true if the actual matches the expected
   */
  boolean match(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual);

  /**
   * Check that the key columns match between the two lists of columns.
   *
   * @param expected expected list of all columns (not just the key columns)
   * @param actual actual list of all columns (not just the key columns)
   * @return true if the actual key columns match the expected ones
   */
  boolean checkKey(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual);
}
