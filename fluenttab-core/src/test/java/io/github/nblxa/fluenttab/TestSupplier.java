package io.github.nblxa.fluenttab;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import org.junit.Test;

public class TestSupplier {

  @Test
  public void test_intSupplier() {
    Tab tab = FluentTab.tab().row((IntSupplier) () -> 1);
    assertThat(tab.rows().iterator()
        .next()
        .vals()
        .iterator()
        .next()
        .eval()).isEqualTo(1);
  }

  @Test
  public void test_longSupplier() {
    Tab tab = FluentTab.tab().row((LongSupplier) () -> 2L);
    assertThat(tab.rows().iterator()
        .next()
        .vals()
        .iterator()
        .next()
        .eval()).isEqualTo(2L);
  }

  @Test
  public void test_doubleSupplier() {
    Tab tab = FluentTab.tab().row((DoubleSupplier) () -> 3.0d);
    assertThat(tab.rows().iterator()
        .next()
        .vals()
        .iterator()
        .next()
        .eval()).isEqualTo(3.0d);
  }

  @Test
  public void test_booleanSupplier() {
    Tab tab = FluentTab.tab().row((BooleanSupplier) () -> true);
    assertThat(tab.rows().iterator()
        .next()
        .vals()
        .iterator()
        .next()
        .eval()).isEqualTo(true);
  }
}
