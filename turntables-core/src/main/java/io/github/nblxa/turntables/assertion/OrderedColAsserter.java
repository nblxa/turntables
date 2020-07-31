package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.List;
import java.util.Objects;

class OrderedColAsserter implements ColAsserter {

  /**
   * Check that the actual columns match the actual ones by number and type.
   *
   * @param expected expected list of columns
   * @param actual actual list of columns
   * @return true if the actual columns have the same types at the same positions
   */
  @Override
  public boolean match(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual) {
    Objects.requireNonNull(expected, "expected");
    Objects.requireNonNull(actual, "actual");
    if (expected.size() != actual.size()) {
      return false;
    }
    for (int i = 0; i < expected.size(); i++) {
      if (!matchCols(expected.get(i), actual.get(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check that the actual key columns match the expected key columns by position, number and type.
   *
   * @param expected expected list of all columns (not just the key columns)
   * @param actual actual list of all columns (not just the key columns)
   * @return true if the number of key columns, their positions and types in actual
   *         are the same as in expected
   */
  @Override
  public boolean checkKey(@NonNull List<Tab.Col> expected, @NonNull List<Tab.Col> actual) {
    int expSize = expected.size();
    int actSize = actual.size();
    int minSize = Math.min(expSize, actSize);
    for (int i = 0; i < minSize; i++) {
      Tab.Col expCol = expected.get(i);
      Tab.Col actCol = actual.get(i);
      if (expCol.isKey() == actCol.isKey()) {
        if (expCol.isKey() && !expCol.typ().equals(actCol.typ())) {
          return false;
        }
      } else {
        return false;
      }
    }
    for (int i = minSize; i < expSize; i++) {
      if (expected.get(i).isKey()) {
        return false;
      }
    }
    for (int i = expSize; i < actSize; i++) {
      if (actual.get(i).isKey()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean matchCols(@NonNull Tab.Col expected, @NonNull Tab.Col actual) {
    return expected.typ().accepts(actual.typ());
  }
}
