package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
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
