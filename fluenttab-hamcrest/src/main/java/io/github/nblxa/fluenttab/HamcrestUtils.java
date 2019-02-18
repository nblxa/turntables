package io.github.nblxa.fluenttab;

import org.hamcrest.Matcher;

public class HamcrestUtils {
  static Matcher<Tab.Val> getValMatcher(Tab.Val expected, String rowRef, String colRef) {
    if (expected instanceof AbstractTab.AbstractAssertionVal) {
      Matcher embeddedMatcher = ((MatcherVal) expected).getMatcher();
      return FunctionalMatcher.matcher(
          v -> embeddedMatcher.matches(v.eval()),
          (v, d) -> {
            d.appendText("Mismatch at " + rowRef + " " + colRef + ":\n");
            embeddedMatcher.describeMismatch(v.eval(), d);
          },
          embeddedMatcher::describeTo);
    }
    return FunctionalMatcher.matcher(
        v -> v.matchesActual(expected),
        (v, d) -> d.appendText("found ").appendValue(v),
        d -> d.appendText("matching ").appendValue(expected));
  }
}
