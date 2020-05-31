package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Objects;

final class AssertionResult {
  enum MatchResult {
    /**
     * Successful match.
     */
    MATCH,
    /**
     * A match was attempted but failed.
     */
    MISMATCH,
    /**
     * No matching was performed yet.
     */
    UNKNOWN;

    static MatchResult of(boolean match) {
      return match ? MATCH : MISMATCH;
    }
  }

  @NonNull
  static final AssertionResult NOT_YET_ASSERTED = new AssertionResult(
      MatchResult.UNKNOWN, MatchResult.UNKNOWN);

  @NonNull
  private MatchResult colsMatched;
  @NonNull
  private MatchResult rowsMatched;

  AssertionResult(@NonNull MatchResult colsMatched, @NonNull MatchResult rowsMatched) {
    this.colsMatched = Objects.requireNonNull(colsMatched, "colsMatched");
    this.rowsMatched = Objects.requireNonNull(rowsMatched, "rowsMatched");
  }

  @NonNull
  MatchResult colsMatched() {
    return colsMatched;
  }

  @NonNull
  MatchResult rowsMatched() {
    return rowsMatched;
  }

  @NonNull
  AssertionResult colsMatched(MatchResult colsMatched) {
    return new AssertionResult(colsMatched, this.rowsMatched);
  }

  @NonNull
  AssertionResult rowsMatched(MatchResult rowsMatched) {
    return new AssertionResult(this.colsMatched, rowsMatched);
  }

  @NonNull
  @Override
  public String toString() {
    return "AssertionResult{" +
        "colsMatched=" + colsMatched +
        ", rowsMatched=" + rowsMatched +
        '}';
  }
}
