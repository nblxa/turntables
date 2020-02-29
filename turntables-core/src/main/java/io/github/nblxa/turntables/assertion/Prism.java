package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.AbstractTab;
import java.util.List;

import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Tab;

/**
 * A prism augments the Tab's representation as expected or actual in an assertion so that
 * it produces a meaningful diff in the tests in case of a mismatch.
 *
 * For example, if matching rows in the {@link Turntables.RowMode#MATCHES_IN_ANY_ORDER} mode,
 * a prism will align the order of rows in actual with the one in expected, where they match,
 * so that the diff of two {@link Tab}s shows only mismatching rows, not the rows
 * that are out of order.
 *
 * equals, hashCode and canEqual are overridden with an exception throw to prevent accidental
 * use in assertions.
 */
public abstract class Prism extends AbstractTab {
  private static final String MSG_NO_EQUALS = "Prism does not support equals";
  private static final String MSG_NO_HASH_CODE = "Prism does not support hashCode";

  Prism(@NonNull List<Col> cols) {
    super(cols);
  }

  /**
   * Get the augmented representation of the underlying {@link Tab} for use in diffs.
   * @return the augmented String representation of the {@link Tab}
   */
  @NonNull
  public final String representation() {
    return toString();
  }

  @Override
  public final boolean equals(Object o) {
    throw new UnsupportedOperationException(MSG_NO_EQUALS);
  }

  @Override
  public final int hashCode() {
    throw new UnsupportedOperationException(MSG_NO_HASH_CODE);
  }

  @Override
  public boolean canEqual(Object other) {
    throw new UnsupportedOperationException(MSG_NO_EQUALS);
  }

  abstract static class PrismRow extends AbstractRow {
    public PrismRow(@NonNull List<Col> cols) {
      super(cols);
    }

    @Override
    public final boolean equals(Object o) {
      throw new UnsupportedOperationException(MSG_NO_EQUALS);
    }

    @Override
    public final int hashCode() {
      throw new UnsupportedOperationException(MSG_NO_HASH_CODE);
    }

    @Override
    public boolean canEqual(Object other) {
      throw new UnsupportedOperationException(MSG_NO_EQUALS);
    }
  }
}
