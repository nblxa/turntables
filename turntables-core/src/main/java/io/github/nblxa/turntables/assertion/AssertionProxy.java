package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Tab;

import java.util.Objects;

public abstract class AssertionProxy extends AbstractTab {

  final Tab tab;
  final Asserter asserter;

  private AssertionProxy(Tab tab, @NonNull Asserter asserter) {
    super(tab.cols());
    this.tab = Objects.requireNonNull(tab, "tab is null");
    this.asserter = Objects.requireNonNull(asserter, "asserter is null");
  }

  public static Builder builder() {
    return new Builder();
  }

  @NonNull
  public abstract String representation();

  @Override
  @NonNull
  public Iterable<Row> rows() {
    return tab.rows();
  }

  @Override
  @SuppressFBWarnings("EQ_UNUSUAL")
  public boolean equals(Object other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    throw new UnsupportedOperationException();
  }

  @NonNull
  @Override
  public String toString() {
    return String.format("%s[asserter=%s, tab=%s]", getClass().getSimpleName(), asserter, tab);
  }

  public interface AssertionBuilder<S extends AssertionBuilder<S>> {
    @NonNull
    S rowMode(@NonNull Turntables.RowMode rowMode);

    @NonNull
    S colMode(@NonNull Turntables.ColMode colMode);

    @NonNull
    S rowPermutationLimit(long rowPermutationLimit);
  }

  public static class Builder
      implements AssertionBuilder<Builder> {

    private Tab expected;
    private Tab actual;
    private Turntables.RowMode rowMode = Turntables.RowMode.MATCHES_IN_GIVEN_ORDER;
    private Turntables.ColMode colMode = Turntables.ColMode.MATCHES_IN_GIVEN_ORDER;
    private long rowPermutationLimit = Turntables.ROW_PERMUTATION_LIMIT;

    // built state
    private volatile Expected expectedProxy;
    private volatile Actual actualProxy;

    private Builder() {
    }

    @NonNull
    public Builder expected(@NonNull Tab expected) {
      this.expected = Objects.requireNonNull(expected, "expected");
      return this;
    }

    @NonNull
    public Builder actual(@NonNull Tab actual) {
      this.actual = Objects.requireNonNull(actual, "actual");
      return this;
    }

    @Override
    @NonNull
    public Builder rowMode(@NonNull Turntables.RowMode rowMode) {
      this.rowMode = Objects.requireNonNull(rowMode, "rowMode");
      return this;
    }

    @Override
    @NonNull
    public Builder colMode(@NonNull Turntables.ColMode colMode) {
      this.colMode = Objects.requireNonNull(colMode, "colMode");
      return this;
    }

    @Override
    @NonNull
    public Builder rowPermutationLimit(long rowPermutationLimit) {
      if (rowPermutationLimit <= 0L) {
        throw new IllegalArgumentException("rowPermutationLimit must be > 0L");
      }
      this.rowPermutationLimit = rowPermutationLimit;
      return this;
    }

    @Override
    @SuppressFBWarnings("EQ_UNUSUAL")
    public boolean equals(Object other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
      throw new UnsupportedOperationException();
    }

    private void build() {
      Conf conf = new Conf(expected, actual, rowMode, colMode, rowPermutationLimit);
      Asserter asserter = Asserter.createAsserter(conf);
      this.expectedProxy = new Expected(expected, asserter);
      this.actualProxy = new Actual(actual, asserter);
    }

    @NonNull
    public Expected buildOrGetExpectedProxy() {
      if (expectedProxy == null) {
        synchronized (this) {
          if (expectedProxy == null) {
            build();
          }
        }
      }
      return expectedProxy;
    }

    @NonNull
    public Actual buildOrGetActualProxy() {
      if (actualProxy == null) {
        synchronized (this) {
          if (actualProxy == null) {
            build();
          }
        }
      }
      return actualProxy;
    }

    @NonNull
    public Builder copy() {
      Builder builder = new Builder();
      builder.actual = actual;
      builder.expected = expected;
      builder.rowMode = rowMode;
      builder.colMode = colMode;
      builder.rowPermutationLimit = rowPermutationLimit;
      return builder;
    }

    @NonNull
    Tab getExpected() {
      Objects.requireNonNull(expected, "expected");
      return expected;
    }
  }

  public static class Conf {
    public final Tab expected;
    public final Tab actual;
    final Turntables.RowMode rowMode;
    final Turntables.ColMode colMode;
    final long rowPermutationLimit;

    Conf(Tab expected, Tab actual, Turntables.RowMode rowMode, Turntables.ColMode colMode,
         long rowPermutationLimit) {
      this.expected = Objects.requireNonNull(expected, "expected");
      this.actual = Objects.requireNonNull(actual, "actual");
      this.rowMode = Objects.requireNonNull(rowMode, "rowMode");
      this.colMode = Objects.requireNonNull(colMode, "colMode");
      this.rowPermutationLimit = rowPermutationLimit;
    }

    @NonNull
    @Override
    public String toString() {
      return String.format("Conf[rowMode=%s, colMode=%s, rowPermutationLimit=%s]", rowMode, colMode, rowPermutationLimit);
    }
  }

  public static class Expected extends AssertionProxy {
    private Expected(Tab expected, Asserter asserter) {
      super(expected, asserter);
    }

    @NonNull
    @Override
    public String representation() {
      Tab rowOrderExp = RowOrderPrism.ofExpected(asserter, tab);
      Tab rowOrderAct = RowOrderPrism.ofActual(asserter, asserter.getConf().actual);
      Tab colNameRowOrdExp = ColNamePrism.ofExpected(asserter, rowOrderExp, rowOrderAct);
      Tab assertValExp = AssertionValPrism.ofExpected(colNameRowOrdExp, rowOrderAct);
      return assertValExp.toString();
    }
  }

  public static class Actual extends AssertionProxy {
    private Actual(Tab actual, Asserter asserter) {
      super(actual, asserter);
    }

    public boolean matchesExpected() {
      return asserter.match();
    }

    @NonNull
    @Override
    public String representation() {
      Tab rowOrderExp = RowOrderPrism.ofExpected(asserter, asserter.getConf().expected);
      Tab rowOrderAct = RowOrderPrism.ofActual(asserter, tab);
      Tab.NamedColTab colNamePrism = ColNamePrism.ofActual(asserter, rowOrderExp, rowOrderAct);
      return colNamePrism.toString();
    }
  }
}
