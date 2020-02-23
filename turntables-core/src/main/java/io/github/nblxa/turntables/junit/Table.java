package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;

public interface Table {
  /**
   * Get the table's name.
   * @return table name
   */
  @NonNull
  String getName();

  /**
   * Action to perform when cleaning up the table in the test data source after each test method
   * or class (depending on whether the table is a Rule or a ClassRule).
   *
   * @return clean-up action to perform for the table
   */
  @NonNull
  CleanUpAction getCleanUpAction();
}
