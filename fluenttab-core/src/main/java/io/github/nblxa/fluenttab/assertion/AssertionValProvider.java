package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.AbstractTab;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface AssertionValProvider {
  @NonNull
  AbstractTab.AbstractAssertionVal assertionVal(@NonNull Object assertionObject);

  @NonNull
  Class<?> getAssertionClass();
}
