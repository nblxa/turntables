package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.Tab;
import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractRowAsserter implements RowAsserter {
  protected final ValAsserter valAsserter;

  AbstractRowAsserter(ValAsserter valAsserter) {
    this.valAsserter = valAsserter;
  }

  boolean matchRows(@NonNull Tab.Row expected, @NonNull Tab.Row actual) {
    return valAsserter.match(expected.vals(), actual.vals());
  }
}
