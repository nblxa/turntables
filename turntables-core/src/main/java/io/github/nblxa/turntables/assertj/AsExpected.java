package io.github.nblxa.turntables.assertj;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.nblxa.turntables.Tab;

public interface AsExpected<T extends Tab> {
  /**
   * Trigger the assertion and return the assertion object.
   *
   * @return the assertion object
   */
  @NonNull
  TabAssert<T> asExpected();
}
