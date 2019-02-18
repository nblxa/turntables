package io.github.nblxa.fluenttab.assertj;

import io.github.nblxa.fluenttab.Tab;

public class TablesAssertj {
  private TablesAssertj() {
  }

  public static <T extends Tab> TabAssert<T> assertThat(T actualTab) {
    return new TabAssert<>(actualTab);
  }
}
