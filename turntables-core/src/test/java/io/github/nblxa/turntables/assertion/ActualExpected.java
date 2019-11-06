package io.github.nblxa.turntables.assertion;

import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;

public class ActualExpected {

  public static Tab tab_2x2() {
    return Turntables.tab()
        .row(1, 2)
        .row(3, 4);
  }

  public static Tab tab_2x2_reversed() {
    return Turntables.tab()
        .row(3, 4)
        .row(1, 2);
  }

  public static Tab actual_2x2_doesnt_match() {
    return Turntables.tab()
        .row(1, 2)
        .row(null, 4);
  }

  public static Tab expected_2x2_assertions() {
    return Turntables.tab()
        .row(1, Turntables.test(i -> true))
        .row(Turntables.test(i -> i instanceof Integer), 4);
  }

  public static Tab tab_2x2_duplicates() {
    return Turntables.tab()
        .row(1, 4)
        .row(1, 4);
  }

  public static Tab tab_1x2() {
    return Turntables.tab()
        .row(1, 2);
  }


  public static Tab actual_10x5() {
    return Turntables.tab()
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
    return Turntables.tab()
        .row(Turntables.testInt(i -> i == 8), 1, 1, 1, 1)
        .row(1, 1, 1, 1, 1)
        .row(1, Turntables.testInt(i -> i == 8), 1, 1, 1)
        .row(1, 1, 1, 1, 1)
        .row(1, 1, Turntables.testInt(i -> i == 8), 1, 1)
        .row(1, 1, 1, 1, 1)
        .row(1, 1, 1, Turntables.testInt(i -> i == 8), 1)
        .row(1, 1, 1, 1, 1)
        .row(1, 1, 1, 1, Turntables.testInt(i -> i == 8))
        .row(1, 1, 1, 1, 1);
  }

  public static Tab actual_10x5_doesnt_match() {
    return Turntables.tab()
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
