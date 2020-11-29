package io.github.nblxa.turntables;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.openjdk.jcstress.infra.results.I_Result;

public class TestIngestionConcurrency {
  @Test
  public void test() {
    IngestionConcurrency ingestionConcurrency = new IngestionConcurrency();
    I_Result r = new I_Result();
    ingestionConcurrency.actor1(r);
    assertThat(r.r1)
        .isOne();
    ingestionConcurrency.actor2(r);
    assertThat(r.r1)
        .isOne();
  }
}
