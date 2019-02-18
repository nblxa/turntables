package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Objects;
import java.util.function.Consumer;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class TableMatcher<T extends Tab> extends TypeSafeDiagnosingMatcher<T>
    implements AssertionProxy.AssertionBuilder<TableMatcher<T>> {

  private final AssertionProxy.Builder assertionProxyBuilder;
  private Consumer<Description> describeTo;

  public TableMatcher(@NonNull Tab expected) {
    Objects.requireNonNull(expected, "expected");
    this.assertionProxyBuilder = AssertionProxy.builder().expected(expected);
    this.describeTo = d -> {
      d.appendText(assertionProxyBuilder.getExpected().toString());
    };
  }

  @Override
  @NonNull
  public TableMatcher<T> rowMode(@NonNull FluentTab.RowMode rowMode) {
    assertionProxyBuilder.rowMode(rowMode);
    return this;
  }

  @Override
  @NonNull
  public TableMatcher<T> colMode(@NonNull FluentTab.ColMode colMode) {
    assertionProxyBuilder.colMode(colMode);
    return this;
  }

  @Override
  @NonNull
  public TableMatcher<T> rowPermutationLimit(long rowPermutationLimit) {
    assertionProxyBuilder.rowPermutationLimit(rowPermutationLimit);
    return this;
  }

  @Override
  protected boolean matchesSafely(T item, Description mismatchDescription) {
    AssertionProxy.Actual actualProxy = assertionProxyBuilder.actual(item)
        .buildOrGetActualProxy();
    boolean matches = actualProxy.matchesExpected();
    String augmentedRepresentation = assertionProxyBuilder.buildOrGetExpectedProxy()
        .representation();
    describeTo = d -> d.appendText("<").appendText(augmentedRepresentation).appendText(">");
    mismatchDescription.appendText("was <")
        .appendText(actualProxy.representation())
        .appendText(">");
    return matches;
  }

  @Override
  public void describeTo(Description description) {
    describeTo.accept(description);
  }
}
