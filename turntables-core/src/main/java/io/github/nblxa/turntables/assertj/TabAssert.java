package io.github.nblxa.turntables.assertj;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.SettingsTransaction;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.assertion.AssertionProxy;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;
import org.assertj.core.internal.Failures;

@SuppressWarnings("java:S2160")
public class TabAssert<T extends Tab> extends AbstractObjectAssert<TabAssert<T>, T>
    implements AssertionProxy.AssertionBuilder<TabAssert<T>>, Settings.Builder<TabAssert<T>> {

  /**
   * One of the patterns recognized by IntelliJ IDEA.
   * See <a href="https://github.com/JetBrains/intellij-community/blob/201.7223/plugins/junit_rt/src/com/intellij/junit4/ExpectedPatterns.java">ExpectedPatterns.java</a>.
   */
  private static final String MSG_FORMAT = "%nEXPECTED: %s%nBUT: WAS %s";

  private final AssertionProxy.Builder proxyBuilder;
  private final Failures failures = Failures.instance();

  public TabAssert(T tab) {
    super(tab, TabAssert.class);
    this.proxyBuilder = AssertionProxy.builder()
        .actual(tab);
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
    try (SettingsTransaction ignored = Turntables.setSettings(builder.getSettings())) {
      AssertionProxy.Actual actProxy = builder.buildOrGetActualProxy();
      if (actProxy.matchesExpected()) {
        return this;
      }
      AssertionProxy.Expected expProxy = builder.buildOrGetExpectedProxy();
      // Use the Representation objects to prevent AssertJ from enclosing the String values in double quotes.
      AssertionProxy.Representation actRep = new AssertionProxy.Representation(actProxy);
      AssertionProxy.Representation expRep = new AssertionProxy.Representation(expProxy);
      ErrorMessageFactory errorMessages = new BasicErrorMessageFactory(MSG_FORMAT, expRep, actRep);

      throw failures.failure(getWritableAssertionInfo(), errorMessages);
    }
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
  public TabAssert<T> decimalMode(@NonNull Settings.DecimalMode decimalMode) {
    proxyBuilder.decimalMode(decimalMode);
    return this;
  }

  @Override
  @NonNull
  public TabAssert<T> nameMode(@NonNull Settings.NameMode nameMode) {
    proxyBuilder.nameMode(nameMode);
    return this;
  }

  @Override
  @NonNull
  public TabAssert<T> rowMode(@NonNull Settings.RowMode rowMode) {
    proxyBuilder.rowMode(rowMode);
    return this;
  }

  @Override
  @NonNull
  public TabAssert<T> colMode(@NonNull Settings.ColMode colMode) {
    proxyBuilder.colMode(colMode);
    return this;
  }

  @Override
  @NonNull
  public TabAssert<T> rowPermutationLimit(long rowPermutationLimit) {
    proxyBuilder.rowPermutationLimit(rowPermutationLimit);
    return this;
  }

  @NonNull
  @Override
  public TabAssert<T> settings(Settings settings) {
    proxyBuilder.settings(settings);
    return this;
  }
}
