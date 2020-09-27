package io.github.nblxa.turntables.junit;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.io.rowstore.CleanUpAction;

public class TestTableDetail {
  @NonNull
  private final Tab tab;
  @NonNull
  private final String name;
  @NonNull
  private final CleanUpAction cleanUpAction;

  public TestTableDetail(@NonNull Tab tab, @NonNull String name, @NonNull CleanUpAction cleanUpAction) {
    this.tab = tab;
    this.name = name;
    this.cleanUpAction = cleanUpAction;
  }

  /**
   * Get the table's name.
   * @return table name
   */
  @NonNull
  public String getName() {
    return name;
  }

  /**
   * Action to perform when cleaning up the table in the test data source after each test method
   * or class (depending on whether the table is a Rule or a ClassRule).
   *
   * @return clean-up action to perform for the table
   */
  @NonNull
  public CleanUpAction getCleanUpAction() {
    return cleanUpAction;
  }

  /**
   * Load data from the table in the test data source and create a new {@link Tab} object
   * to hold it.
   *
   * @return the new {@link Tab} object for use in assertions
   */
  @NonNull
  public Tab getTab() {
    return tab;
  }
}
