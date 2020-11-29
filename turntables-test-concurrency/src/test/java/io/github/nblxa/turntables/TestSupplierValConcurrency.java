package io.github.nblxa.turntables;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.openjdk.jcstress.infra.results.II_Result;

public class TestSupplierValConcurrency {
  @Test
  public void test() {
    SupplierValConcurrency supplierValConcurrency = new SupplierValConcurrency();
    II_Result r = new II_Result();
    supplierValConcurrency.actor1(r);
    assertThat(r.r1)
        .isZero();
    supplierValConcurrency.actor2(r);
    assertThat(r.r2)
        .isZero();
  }
}
