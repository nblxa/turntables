package io.github.nblxa.turntables;

import io.github.nblxa.turntables.assertion.TableMatcher;

public class TablesHamcrest {

  public static <T extends Tab> TableMatcher<T> matchesTab(T expected) {
    return new TableMatcher<>(expected);
  }
}
