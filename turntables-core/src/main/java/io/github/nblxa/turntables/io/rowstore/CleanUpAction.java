package io.github.nblxa.turntables.io.rowstore;

/**
 * The action to perform when cleaning up a table after a test.
 */
public enum CleanUpAction {
  /**
   * Default: {@link #DROP}.
   */
  DEFAULT,

  /**
   * Drop the table.
   */
  DROP,

  /**
   * Truncate the table.
   */
  TRUNCATE,

  /**
   * Delete all data from the table using the DELETE DML statement..
   */
  DELETE,

  /**
   * Turntables does not automatically clean up the table after the test:
   * assuming this is done elsewhere.
   */
  NONE
}
