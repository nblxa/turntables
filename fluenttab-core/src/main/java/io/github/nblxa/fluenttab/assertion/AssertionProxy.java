package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.AbstractTab;
import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Objects;

public abstract class AssertionProxy extends AbstractTab {

  final Tab tab;
  final Conf conf;

  private AssertionProxy(Tab tab, Conf conf) {
    super(tab.cols());
    this.tab = Objects.requireNonNull(tab, "tab");
    this.conf = Objects.requireNonNull(conf, "conf");
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  @NonNull
  public Iterable<Tab.Col> cols() {
    throw new UnsupportedOperationException();
  }

  @Override
  @NonNull
  public Iterable<Tab.Row> rows() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException();
  }

  public interface AssertionBuilder<S extends AssertionBuilder<S>> {
    @NonNull
    S rowMode(@NonNull FluentTab.RowMode rowMode);

    @NonNull
    S colMode(@NonNull FluentTab.ColMode colMode);

    @NonNull
    S rowPermutationLimit(long rowPermutationLimit);
  }

  public static class Builder
      implements AssertionBuilder<Builder> {

    private Tab expected;
    private Tab actual;
    private FluentTab.RowMode rowMode = FluentTab.RowMode.MATCHES_IN_GIVEN_ORDER;
    private FluentTab.ColMode colMode = FluentTab.ColMode.MATCHES_IN_GIVEN_ORDER;
    private long rowPermutationLimit = FluentTab.ROW_PERMUTATION_LIMIT;

    // built state
    private Conf conf;
    private Expected expectedProxy;
    private Actual actualProxy;

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
    public Builder rowMode(@NonNull FluentTab.RowMode rowMode) {
      this.rowMode = Objects.requireNonNull(rowMode, "rowMode");
      return this;
    }

    @Override
    @NonNull
    public Builder colMode(@NonNull FluentTab.ColMode colMode) {
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
    public boolean equals(Object other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
      throw new UnsupportedOperationException();
    }

    private void build() {
      Conf conf = new Conf(expected, actual, rowMode, colMode, rowPermutationLimit);
      this.expectedProxy = new Expected(expected, conf);
      this.actualProxy = new Actual(actual, conf);
      this.expectedProxy.setActual(actualProxy);
      this.conf = conf;
    }

    @NonNull
    public Expected buildOrGetExpectedProxy() {
      synchronized (this) {
        if (conf == null) {
          build();
        }
      }
      return expectedProxy;
    }

    @NonNull
    public Actual buildOrGetActualProxy() {
      synchronized (this) {
        if (conf == null) {
          build();
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
    final FluentTab.RowMode rowMode;
    final FluentTab.ColMode colMode;
    final long rowPermutationLimit;

    Conf(Tab expected, Tab actual, FluentTab.RowMode rowMode, FluentTab.ColMode colMode,
         long rowPermutationLimit) {
      this.expected = Objects.requireNonNull(expected, "expected");
      this.actual = Objects.requireNonNull(actual, "actual");
      this.rowMode = Objects.requireNonNull(rowMode, "rowMode");
      this.colMode = Objects.requireNonNull(colMode, "colMode");
      this.rowPermutationLimit = rowPermutationLimit;
    }
  }

  public static class Expected extends AssertionProxy {
    @NonNull
    private Actual actual;

    private Expected(Tab tab, Conf conf) {
      super(tab, conf);
    }

    private void setActual(Actual actual) {
      this.actual = Objects.requireNonNull(actual, "actual");
    }

    @NonNull
    public String representation() {
      Tab rowOrderExp = RowOrderPrism.ofExpected(actual.asserter);
      Tab rowOrderAct = RowOrderPrism.ofActual(actual.asserter);
      return new ExpectedAssertionValPrism(rowOrderExp, rowOrderAct).toString();
    }

    @Override
    @NonNull
    public Iterable<Row> rows() {
      return tab.rows();
    }
  }

  public static class Actual extends AssertionProxy {
    @NonNull
    private final Asserter asserter;

    private Actual(Tab tab, Conf conf) {
      super(tab, conf);
      this.asserter = Asserter.createAsserter(conf);
    }

    public boolean matchesExpected() {
      return asserter.match();
    }

    @NonNull
    public String representation() {
      return RowOrderPrism.ofActual(asserter).toString();
    }

    @Override
    @NonNull
    public Iterable<Row> rows() {
      return tab.rows();
    }
  }
}
