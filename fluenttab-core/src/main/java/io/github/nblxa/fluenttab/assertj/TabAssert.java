package io.github.nblxa.fluenttab.assertj;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;
import io.github.nblxa.fluenttab.assertion.AssertionProxy;
import org.assertj.core.annotations.NonNull;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.WritableAssertionInfo;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;
import org.assertj.core.internal.Failures;

public class TabAssert<T extends Tab> extends AbstractObjectAssert<TabAssert<T>, T>
    implements AssertionProxy.AssertionBuilder<TabAssert<T>> {
  private final AssertionProxy.Builder proxyBuilder;
  private final AssertionInfo info;
  private final Failures failures = Failures.instance();

  TabAssert(T tab) {
    super(tab, TabAssert.class);
    this.proxyBuilder = AssertionProxy.builder()
        .actual(tab);
    this.info = new WritableAssertionInfo(null);
  }

  @NonNull
  public <U extends Tab> TabAssert<T> matches(@NonNull U expectedTab) {
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
        "%nExpecting:%n <%s>%nto match:%n <%s>%nbut a mismatch was found.", actRep, expRep);
    throw failures.failure(info, errorMessages);
  }

  @Override
  @NonNull
  public TabAssert<T> rowMode(@NonNull FluentTab.RowMode rowMode) {
    proxyBuilder.rowMode(rowMode);
    return this;
  }

  @Override
  @NonNull
  public TabAssert<T> colMode(@NonNull FluentTab.ColMode colMode) {
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
