package io.github.nblxa.turntables.assertj;

import io.github.nblxa.turntables.Tab;

public class FtAssertj {
  private FtAssertj() {
  }

  public static <T extends Tab> TabAssert<T> assertThat(T actualTab) {
    return new TabAssert<>(actualTab);
  }
}
