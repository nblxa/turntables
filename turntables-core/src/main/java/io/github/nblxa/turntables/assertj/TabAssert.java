package io.github.nblxa.turntables.assertj;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.assertion.AssertionProxy;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.WritableAssertionInfo;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;
import org.assertj.core.internal.Failures;

public class TabAssert<T extends Tab> extends AbstractObjectAssert<TabAssert<T>, T>
    implements AssertionProxy.AssertionBuilder<TabAssert<T>> {
  private static final String MSG_FORMAT = "%nExpected: <%s>%nbut was: <%s>%n";

  private final AssertionProxy.Builder proxyBuilder;
  private final AssertionInfo info;
  private final Failures failures = Failures.instance();

  public TabAssert(T tab) {
    super(tab, TabAssert.class);
    this.proxyBuilder = AssertionProxy.builder()
        .actual(tab);
    this.info = new WritableAssertionInfo(null);
  }

  /**
   * Asserts that the actual {@link Tab} matches the given expected one.
   *
   * <p>In case of a mismatch, an {@link AssertionError} will be thrown by AssertJ.
   *
   * @param expectedTab the expected tab
   * @param <U> subtype of the {@link Tab}
   * @return the assertion object
   */
  @NonNull
  public <U extends Tab> TabAssert<T> matchesExpected(@NonNull U expectedTab) {
    AssertionProxy.Builder builder = proxyBuilder.copy();
    builder.expected(expectedTab);
    AssertionProxy.Actual actProxy = builder.buildOrGetActualProxy();
    if (actProxy.matchesExpected()) {
      return this;
    }
    AssertionProxy.Expected expProxy = builder.buildOrGetExpectedProxy();
    String actRep = actProxy.representation();
    String expRep = expProxy.representation();
    ErrorMessageFactory errorMessages = new BasicErrorMessageFactory(
        MSG_FORMAT, expRep, actRep);

    throw failures.failure(info, errorMessages);
  }

  /**
   * Starts a fluent construction of the expected {@link Tab}.
   *
   * As soon as the expected {@link Tab} is created, the assertion can be triggered
   * by calling {@link TabAssertColAdder#asExpected}.
   *
   * @return the builder object constructing the expected {@link Tab}
   */
  @NonNull
  public TabAssertColAdder<T> matches() {
    return new TabAssertColAdder<>(this, Turntables.tab());
  }

  @Override
  @NonNull
  public TabAssert<T> rowMode(@NonNull Turntables.RowMode rowMode) {
    proxyBuilder.rowMode(rowMode);
    return this;
  }

  @Override
  @NonNull
  public TabAssert<T> colMode(@NonNull Turntables.ColMode colMode) {
    proxyBuilder.colMode(colMode);
    return this;
  }

  @Override
  @NonNull
  public TabAssert<T> rowPermutationLimit(long rowPermutationLimit) {
    proxyBuilder.rowPermutationLimit(rowPermutationLimit);
    return this;
  }
}
