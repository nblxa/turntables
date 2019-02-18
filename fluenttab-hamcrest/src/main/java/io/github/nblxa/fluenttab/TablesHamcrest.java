package io.github.nblxa.fluenttab;

import io.github.nblxa.fluenttab.assertion.TableMatcher;

public class TablesHamcrest {

  public static <T extends Tab> TableMatcher<T> matchesTab(T expected) {
    return new TableMatcher<>(expected);
  }
}
