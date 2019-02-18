package io.github.nblxa.fluenttab.assertion;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;

public class ActualExpected {

  public static Tab tab_2x2() {
    return FluentTab.tab()
        .row(1, 2)
        .row(3, 4);
  }

  public static Tab tab_2x2_reversed() {
    return FluentTab.tab()
        .row(3, 4)
        .row(1, 2);
  }

  public static Tab actual_2x2_doesnt_match() {
    return FluentTab.tab()
        .row(1, 2)
        .row(null, 4);
  }

  public static Tab expected_2x2_assertions() {
    return FluentTab.tab()
        .row(1, FluentTab.test(i -> true))
        .row(FluentTab.test(i -> i instanceof Integer), 4);
  }

  public static Tab tab_2x2_duplicates() {
    return FluentTab.tab()
        .row(1, 4)
        .row(1, 4);
  }

  public static Tab tab_1x2() {
    return FluentTab.tab()
        .row(1, 2);
  }


  public static Tab actual_10x5() {
    return FluentTab.tab()
        .row(1, 1, 1, 1, 1)
        .row(1, 1, 1, 1, 8)
        .row(1, 1, 1, 1, 1)
        .row(1, 1, 1, 8, 1)
        .row(1, 1, 1, 1, 1)
        .row(1, 1, 8, 1, 1)
        .row(1, 1, 1, 1, 1)
        .row(1, 8, 1, 1, 1)
        .row(1, 1, 1, 1, 1)
        .row(8, 1, 1, 1, 1);
  }

  public static Tab expected_10x5_assertions() {
    return FluentTab.tab()
        .row(FluentTab.testInt(i -> i == 8), 1, 1, 1, 1)
        .row(1, 1, 1, 1, 1)
        .row(1, FluentTab.testInt(i -> i == 8), 1, 1, 1)
        .row(1, 1, 1, 1, 1)
        .row(1, 1, FluentTab.testInt(i -> i == 8), 1, 1)
        .row(1, 1, 1, 1, 1)
        .row(1, 1, 1, FluentTab.testInt(i -> i == 8), 1)
        .row(1, 1, 1, 1, 1)
        .row(1, 1, 1, 1, FluentTab.testInt(i -> i == 8))
        .row(1, 1, 1, 1, 1);
  }

  public static Tab actual_10x5_doesnt_match() {
    return FluentTab.tab()
        .row(1, 1, 1, 1, 1)
        .row(1, 1, 1, 1, 8)
        .row(1, 1, 1, 1, 1)
        .row(1, 1, 1, 8, 1)
        .row(1, 1, 1, 1, 1)
        .row(1, 1, 8, 1, 1)
        .row(1, 1, 1, 1, 1)
        .row(1, 8, 1, 1, 1)
        .row(1, 1, 1, 1, -1000)
        .row(8, 1, 1, 1, 1);
  }
}
