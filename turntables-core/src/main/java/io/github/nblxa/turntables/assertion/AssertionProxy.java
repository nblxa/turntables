package io.github.nblxa.turntables.assertion;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.nblxa.turntables.AbstractTab;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Tab;

import java.util.List;
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
  public List<Row> rows() {
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
    /**
     * Specify the mode in which to match rows.
     * <p>Default is {@link Turntables.RowMode#MATCHES_IN_GIVEN_ORDER}.
     * @param rowMode row mode
     * @return the assertion object
     */
    @NonNull
    S rowMode(@NonNull Turntables.RowMode rowMode);

    /**
     * Specify the mode in which to match columns.
     * <p>Default is {@link Turntables.ColMode#MATCHES_IN_GIVEN_ORDER}.
     * @param colMode column mode
     * @return the assertion object
     */
    @NonNull
    S colMode(@NonNull Turntables.ColMode colMode);

    /**
     * Specify the maximum number of row sequence permutations to check for
     * when matching rows in any order.
     * <p>Default is 10,000.
     * @param rowPermutationLimit maximum number of row permutations to try to match
     * @return the assertion object
     */
    @NonNull
    S rowPermutationLimit(long rowPermutationLimit);

    /**
     * Specify a non-default {@link Settings} to use for the assertion.
     *
     * <p>The config is stored in an {@link InheritableThreadLocal}, so only one instance can be used
     * in a single thread at a single moment.
     *
     * <p>If {@code null} is passed, the value will be set to the default.
     *
     * @param settings the config to the be used
     * @return the assertion object
     */
    @NonNull
    S settings(Settings settings);
  }

  public static class Builder
      implements AssertionBuilder<Builder> {

    private Tab expected;
    private Tab actual;
    private Turntables.RowMode rowMode = Turntables.RowMode.MATCHES_IN_GIVEN_ORDER;
    private Turntables.ColMode colMode = Turntables.ColMode.MATCHES_IN_GIVEN_ORDER;
    private long rowPermutationLimit = Turntables.ROW_PERMUTATION_LIMIT;
    private Settings settings = Turntables.getSettings();

    // built state
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

    @NonNull
    @Override
    public Builder settings(@Nullable Settings settings) {
      this.settings = settings;
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
      Conf conf = new Conf(expected, actual, rowMode, colMode, rowPermutationLimit, settings);
      Asserter asserter = Asserter.createAsserter(conf);
      this.expectedProxy = new Expected(expected, asserter);
      this.actualProxy = new Actual(actual, asserter);
    }

    @NonNull
    public Expected buildOrGetExpectedProxy() {
      if (expectedProxy == null) {
        build();
      }
      return expectedProxy;
    }

    @NonNull
    public Actual buildOrGetActualProxy() {
      if (actualProxy == null) {
        build();
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
  }

  public static class Conf {
    public final Tab expected;
    public final Tab actual;
    final Turntables.RowMode rowMode;
    final Turntables.ColMode colMode;
    final long rowPermutationLimit;
    final Settings settings;

    Conf(Tab expected, Tab actual, Turntables.RowMode rowMode, Turntables.ColMode colMode,
         long rowPermutationLimit, Settings settings) {
      this.expected = Objects.requireNonNull(expected, "expected");
      this.actual = Objects.requireNonNull(actual, "actual");
      this.rowMode = Objects.requireNonNull(rowMode, "rowMode");
      this.colMode = Objects.requireNonNull(colMode, "colMode");
      this.rowPermutationLimit = rowPermutationLimit;
      this.settings = Objects.requireNonNull(settings, "settings");
    }

    @NonNull
    @Override
    public String toString() {
      return String.format("Conf[rowMode=%s, colMode=%s, rowPermutationLimit=%s, settings=%s]",
          rowMode, colMode, rowPermutationLimit, settings);
    }
  }

  public static class Expected extends AssertionProxy {
    private Expected(Tab expected, Asserter asserter) {
      super(expected, asserter);
    }

    @NonNull
    @Override
    public String representation() {
      Prism rowOrderExp = RowPrismFactory.createFromExpected(asserter, tab);
      Prism rowOrderAct = RowPrismFactory.createFromActual(asserter, asserter.getConf().actual);
      Prism colNameRowOrdExp = ColPrismFactory.createFromExpected(asserter, rowOrderExp, rowOrderAct);
      Prism assertValExp = AssertionValPrism.createFromExpected(colNameRowOrdExp, rowOrderAct);
      Prism render = RenderPrism.createFrom(assertValExp, asserter);
      return render.representation();
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
      Prism rowOrderExp = RowPrismFactory.createFromExpected(asserter, asserter.getConf().expected);
      Prism rowOrderAct = RowPrismFactory.createFromActual(asserter, tab);
      Prism colOrderAct = ColPrismFactory.createFromActual(asserter, rowOrderExp, rowOrderAct);
      Prism render = RenderPrism.createFrom(colOrderAct, asserter);
      return render.representation();
    }
  }

  public static class Representation {
    @NonNull
    private final AssertionProxy assertionProxy;

    public Representation(@NonNull AssertionProxy assertionProxy) {
      this.assertionProxy = Objects.requireNonNull(assertionProxy, "assertionProxy");
    }

    @NonNull
    @Override
    public String toString() {
      return assertionProxy.representation();
    }
  }
}
