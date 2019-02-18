package io.github.nblxa.fluenttab;

import io.github.nblxa.fluenttab.assertion.AssertionValProvider;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.hamcrest.Matcher;

public class HamcrestAssertionValProvider implements AssertionValProvider {
  @Override
  @NonNull
  public AbstractTab.AbstractAssertionVal assertionVal(@NonNull Object assertionObject) {
    if (!(assertionObject instanceof Matcher)) {
      throw new IllegalArgumentException("assertionObject is not an org.hamcrest.Matcher");
    }
    Matcher<?> matcher = (Matcher<?>) assertionObject;
    return new MatcherVal(matcher);
  }

  @NonNull
  public Class<?> getAssertionClass() {
    return Matcher.class;
  }
}
