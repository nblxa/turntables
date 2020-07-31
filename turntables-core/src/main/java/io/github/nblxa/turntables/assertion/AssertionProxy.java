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
      implements AssertionBuilder<Builder>, Settings.Builder<AssertionProxy.Builder> {

    private Tab expected;
    private Tab actual;
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

    @NonNull
    @Override
    public Builder decimalMode(@NonNull Settings.DecimalMode decimalMode) {
      settings = settings.getBuilder().decimalMode(decimalMode).build();
      return this;
    }

    @NonNull
    @Override
    public Builder nameMode(@NonNull Settings.NameMode nameMode) {
      settings = settings.getBuilder().nameMode(nameMode).build();
      return this;
    }

    @NonNull
    @Override
    public Builder rowMode(@NonNull Settings.RowMode rowMode) {
      settings = settings.getBuilder().rowMode(rowMode).build();
      return this;
    }

    @NonNull
    @Override
    public Builder colMode(@NonNull Settings.ColMode colMode) {
      settings = settings.getBuilder().colMode(colMode).build();
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

    @NonNull
    public Expected buildOrGetExpectedProxy() {
      if (expectedProxy == null) {
        buildProxies();
      }
      return expectedProxy;
    }

    @NonNull
    public Actual buildOrGetActualProxy() {
      if (actualProxy == null) {
        buildProxies();
      }
      return actualProxy;
    }

    @NonNull
    public Builder copy() {
      Builder builder = new Builder();
      builder.actual = actual;
      builder.expected = expected;
      builder.rowPermutationLimit = rowPermutationLimit;
      builder.settings = settings;
      return builder;
    }

    @NonNull
    public Settings getSettings() {
      return settings;
    }

    private void buildProxies() {
      Conf conf = new Conf(expected, actual, rowPermutationLimit, settings);
      Asserter asserter = Asserter.createAsserter(conf);
      this.expectedProxy = new Expected(expected, asserter);
      this.actualProxy = new Actual(actual, asserter);
    }
  }

  public static class Conf {
    public final Tab expected;
    public final Tab actual;
    final long rowPermutationLimit;
    final Settings settings;

    Conf(Tab expected, Tab actual, long rowPermutationLimit, Settings settings) {
      this.expected = Objects.requireNonNull(expected, "expected");
      this.actual = Objects.requireNonNull(actual, "actual");
      this.rowPermutationLimit = rowPermutationLimit;
      this.settings = Objects.requireNonNull(settings, "settings");
    }

    @NonNull
    @Override
    public String toString() {
      return String.format("Conf[rowPermutationLimit=%s, settings=%s]",
          rowPermutationLimit, settings);
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
