package io.github.nblxa.fluenttab.test;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.nblxa.fluenttab.Tab;
import io.github.nblxa.fluenttab.FluentTab;
import io.github.nblxa.fluenttab.Typ;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import org.junit.Test;

public class TestTypes {

  @Test
  public void test_integer() {
    Tab tab = FluentTab.tab()
        .row(0)
        .row(1)
        .row(-1)
        .row(Integer.MIN_VALUE)
        .row(Integer.MAX_VALUE)
        .row(new Integer(0))
        .row(new Integer(1))
        .row(new Integer(-1))
        .row(new Integer(Integer.MIN_VALUE))
        .row(new Integer(Integer.MAX_VALUE))
        .row((IntSupplier) () -> 1)
        .row(null)
        .row(FluentTab.testInt(i -> i > 10))
        .row(FluentTab.test(o -> true));

    assertHasSingleColOfTyp(tab, Typ.INTEGER);
  }

  @Test
  public void test_long() {
    Tab tab = FluentTab.tab()
        .row(0L)
        .row(1L)
        .row(-1L)
        .row(Long.MIN_VALUE)
        .row(Long.MAX_VALUE)
        .row(new Long(0L))
        .row(new Long(1L))
        .row(new Long(-1L))
        .row(new Long(Long.MIN_VALUE))
        .row(new Long(Long.MAX_VALUE))
        .row((LongSupplier) () -> 1L)
        .row(null)
        .row(FluentTab.testLong(l -> l > 10L))
        .row(FluentTab.test(o -> true));

    assertHasSingleColOfTyp(tab, Typ.LONG);
  }

  @Test
  public void test_boolean() {
    Tab tab = FluentTab.tab()
        .row(true)
        .row(false)
        .row(new Boolean(true))
        .row(new Boolean(false))
        .row((BooleanSupplier) () -> true)
        .row(null)
        .row(FluentTab.test(o -> true));

    assertHasSingleColOfTyp(tab, Typ.BOOLEAN);
  }

  @Test
  public void test_date() {
    Tab tab = FluentTab.tab()
        .row(LocalDate.of(2019, 1, 1))
        .row(null)
        .row(FluentTab.test(o -> true));

    assertHasSingleColOfTyp(tab, Typ.DATE);
  }

  @Test
  public void test_dateTime() {
    Tab tab = FluentTab.tab()
        .row(LocalDateTime.of(2019, 1, 1, 10, 40, 50))
        .row(null)
        .row(FluentTab.test(o -> true));

    assertHasSingleColOfTyp(tab, Typ.DATETIME);
  }

  @Test
  public void test_string() {
    CharSequence cs = new StringBuilder("yo").append(100).append(true).append('L').append(0L);
    FluentTab.tab()
        .row("text")
        .row("")
        .row(null)
        .row(cs)
        .row(FluentTab.test(o -> true));
  }

  @Test
  public void test_double() {
    Tab tab = FluentTab.tab()
        .row(0.0)
        .row(-1.0)
        .row(1.0)
        .row(-1.0)
        .row(Double.MIN_VALUE)
        .row(Double.MAX_VALUE)
        .row(Double.MIN_NORMAL)
        .row(-Double.MIN_NORMAL)
        .row(Double.NEGATIVE_INFINITY)
        .row(Double.POSITIVE_INFINITY)
        .row(Double.NaN)
        .row(new Double(0.0))
        .row(new Double(-1.0))
        .row(new Double(Double.MIN_VALUE))
        .row(new Double(Double.MAX_VALUE))
        .row(new Double(Double.MIN_NORMAL))
        .row(new Double(Double.NEGATIVE_INFINITY))
        .row(new Double(Double.POSITIVE_INFINITY))
        .row(new Double(Double.NaN))
        .row(0.0d)
        .row(new Double(0.0d))
        .row(null)
        .row(FluentTab.testDouble(d -> d > 3.14d))
        .row(FluentTab.test(o -> true));

    assertHasSingleColOfTyp(tab, Typ.DOUBLE);
  }

  private void assertHasSingleColOfTyp(Tab tab, Typ typ) {
    assertThat(tab).isNotNull();
    assertThat(tab.cols()).hasSize(1);

    Tab.Col col = tab.cols().iterator().next();
    assertThat(col).isNotNull()
        .extracting(Tab.Col::typ)
        .isEqualTo(typ);
  }
}
