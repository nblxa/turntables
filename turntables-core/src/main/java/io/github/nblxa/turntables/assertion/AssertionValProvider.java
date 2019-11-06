package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.AbstractTab;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface AssertionValProvider {
  @NonNull
  AbstractTab.AbstractAssertionVal assertionVal(@NonNull Object assertionObject);

  @NonNull
  Class<?> getAssertionClass();
}
