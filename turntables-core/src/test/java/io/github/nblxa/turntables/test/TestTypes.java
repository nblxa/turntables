package io.github.nblxa.turntables.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.nblxa.turntables.Settings;
import io.github.nblxa.turntables.SettingsTransaction;
import io.github.nblxa.turntables.Tab;
import io.github.nblxa.turntables.Turntables;
import io.github.nblxa.turntables.Typ;
import io.github.nblxa.turntables.exception.AssertionEvaluationException;
import io.github.nblxa.turntables.exception.StructureException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;
import org.junit.Test;

public class TestTypes {

  @Test
  public void test_integer() {
    Tab tab = Turntables.tab()
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
        .row(Turntables.testInt(i -> i > 10))
        .row(Turntables.test(o -> true));

    assertValuesAreInstancesOf(tab, Integer.class);

    assertHasSingleColOfTyp(Typ.INTEGER, tab);
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(0));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(1));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(-1));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(new Integer(0)));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(new Integer(1)));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(new Integer(-1)));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(new Integer(Integer.MIN_VALUE)));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(new Integer(Integer.MAX_VALUE)));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row((IntSupplier) () -> 1));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(null).row(0));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(Turntables.testInt(i -> i > 10)));
    assertHasSingleColOfTyp(Typ.INTEGER, Turntables.tab().row(Turntables.test(o -> true)).row(0));
  }

  @Test
  public void test_long() {
    Tab tab = Turntables.tab()
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
        .row(Turntables.testLong(l -> l > 10L))
        .row(Turntables.test(o -> true));

    assertValuesAreInstancesOf(tab, Long.class);

    assertHasSingleColOfTyp(Typ.LONG, tab);
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(0L));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(1L));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(-1L));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(Long.MIN_VALUE));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(Long.MAX_VALUE));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(new Long(0L)));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(new Long(1L)));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(new Long(-1L)));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(new Long(Long.MIN_VALUE)));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(new Long(Long.MAX_VALUE)));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row((LongSupplier) () -> 1L));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(null).row(0L));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(Turntables.testLong(l -> l > 10L)));
    assertHasSingleColOfTyp(Typ.LONG, Turntables.tab().row(Turntables.test(o -> true)).row(0L));
  }

  @Test
  public void test_boolean() {
    Tab tab = Turntables.tab()
        .row(true)
        .row(false)
        .row(new Boolean(true))
        .row(new Boolean(false))
        .row((BooleanSupplier) () -> true)
        .row(null)
        .row(Turntables.test(o -> true));

    assertValuesAreInstancesOf(tab, Boolean.class);

    assertHasSingleColOfTyp(Typ.BOOLEAN, tab);
    assertHasSingleColOfTyp(Typ.BOOLEAN, Turntables.tab().row(true));
    assertHasSingleColOfTyp(Typ.BOOLEAN, Turntables.tab().row(false));
    assertHasSingleColOfTyp(Typ.BOOLEAN, Turntables.tab().row(new Boolean(true)));
    assertHasSingleColOfTyp(Typ.BOOLEAN, Turntables.tab().row(new Boolean(false)));
    assertHasSingleColOfTyp(Typ.BOOLEAN, Turntables.tab().row((BooleanSupplier) () -> true));
    assertHasSingleColOfTyp(Typ.BOOLEAN, Turntables.tab().row(null).row(true));
    assertHasSingleColOfTyp(Typ.BOOLEAN, Turntables.tab().row(Turntables.test(o -> true)).row(true));
  }

  @Test
  public void test_date() {
    LocalDate ld = LocalDate.of(2019, 1, 1);
    java.util.Date jud = java.util.Date.from(ld.atStartOfDay().toInstant(ZoneOffset.UTC));
    java.sql.Date jsd = new java.sql.Date(jud.getTime());
    Tab tab = Turntables.tab()
        .row(ld)
        .row(jud)
        .row(jsd)
        .row(null)
        .row(Turntables.test(o -> true));

    assertValuesAreInstancesOf(tab, LocalDate.class);

    assertHasSingleColOfTyp(Typ.DATE, tab);
    assertHasSingleColOfTyp(Typ.DATE, Turntables.tab().row(ld));
    assertHasSingleColOfTyp(Typ.DATE, Turntables.tab().row(jud));
    assertHasSingleColOfTyp(Typ.DATE, Turntables.tab().row(jsd));
    assertHasSingleColOfTyp(Typ.DATE, Turntables.tab().row(null).row(ld));
    assertHasSingleColOfTyp(Typ.DATE, Turntables.tab().row(Turntables.test(o -> true)).row(ld));
  }

  @Test
  public void test_dateTime() {
    LocalDateTime ldt = LocalDateTime.of(2019, 1, 1, 10, 40, 50);
    java.sql.Timestamp jst = java.sql.Timestamp.from(ldt.toInstant(ZoneOffset.UTC));
    Tab tab = Turntables.tab()
        .row(ldt)
        .row(jst)
        .row(null)
        .row(Turntables.test(o -> true));

    assertValuesAreInstancesOf(tab, LocalDateTime.class);

    assertHasSingleColOfTyp(Typ.DATETIME, tab);
    assertHasSingleColOfTyp(Typ.DATETIME, Turntables.tab().row(ldt));
    assertHasSingleColOfTyp(Typ.DATETIME, Turntables.tab().row(jst));
    assertHasSingleColOfTyp(Typ.DATETIME, Turntables.tab().row(null).row(ldt));
    assertHasSingleColOfTyp(Typ.DATETIME, Turntables.tab().row(Turntables.test(o -> true)).row(ldt));
  }

  @Test
  public void test_string() {
    CharSequence cs = new StringBuilder("yo").append(100).append(true).append('L').append(0L);
    Tab tab = Turntables.tab()
        .row("text")
        .row("")
        .row(null)
        .row(cs)
        .row(Turntables.test(o -> true))
        .row(Pattern.compile("^abc$"));

    assertValuesAreInstancesOf(tab, String.class);

    assertHasSingleColOfTyp(Typ.STRING, tab);
    assertHasSingleColOfTyp(Typ.STRING, Turntables.tab().row("text"));
    assertHasSingleColOfTyp(Typ.STRING, Turntables.tab().row(""));
    assertHasSingleColOfTyp(Typ.STRING, Turntables.tab().row(null).row("text"));
    assertHasSingleColOfTyp(Typ.STRING, Turntables.tab().row(cs));
    assertHasSingleColOfTyp(Typ.STRING, Turntables.tab().row(Turntables.test(o -> true)).row("text"));
    assertHasSingleColOfTyp(Typ.STRING, Turntables.tab().row(Pattern.compile("^abc$")));
  }

  @Test
  public void test_double() {
    Tab tab = Turntables.tab()
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
        .row(Turntables.testDouble(d -> d > 3.14d))
        .row((DoubleSupplier) () -> 6.0d)
        .row(Turntables.test(o -> true));

    assertValuesAreInstancesOf(tab, Double.class);

    assertHasSingleColOfTyp(Typ.DOUBLE, tab);
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(0.0));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(-1.0));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(1.0));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(-1.0));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(Double.MIN_VALUE));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(Double.MAX_VALUE));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(Double.MIN_NORMAL));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(-Double.MIN_NORMAL));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(Double.NEGATIVE_INFINITY));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(Double.POSITIVE_INFINITY));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(Double.NaN));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(new Double(0.0)));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(new Double(-1.0)));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(new Double(Double.MIN_VALUE)));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(new Double(Double.MAX_VALUE)));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(new Double(Double.NEGATIVE_INFINITY)));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(new Double(Double.POSITIVE_INFINITY)));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(new Double(Double.NaN)));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(0.0d));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(new Double(0.0d)));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(null).row(0.0));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(Turntables.testDouble(d -> d > 3.14d)));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row((DoubleSupplier) () -> 6.0d));
    assertHasSingleColOfTyp(Typ.DOUBLE, Turntables.tab().row(Turntables.test(o -> true)).row(0.0));
  }

  @Test
  public void test_decimal() {
    Tab tab = Turntables.tab()
        .row(BigDecimal.valueOf(100500L))
        .row(BigInteger.valueOf(42L))
        .row(null);

    assertValuesAreInstancesOf(tab, BigDecimal.class);

    assertHasSingleColOfTyp(Typ.DECIMAL, Turntables.tab()
        .row(BigDecimal.valueOf(100500L))
        .row(null));
    assertHasSingleColOfTyp(Typ.DECIMAL, Turntables.tab()
        .row(null)
        .row(BigDecimal.valueOf(100400L)));
  }

  @Test
  public void test_decimalExact() {
    Object[] values = {1, 2L, 3.0d, (IntSupplier) () -> 4, (LongSupplier) () -> 5L,
        (DoubleSupplier) () -> 6.0d};
    for (Object value : values) {
      Throwable t = catchThrowable(() -> Turntables.tab().col(Typ.DECIMAL).row(value));
      assertThat(t)
          .isExactlyInstanceOf(StructureException.class)
          .getCause()
          .isExactlyInstanceOf(StructureException.class)
          .hasMessageStartingWith("Expected decimal or compatible but got ");
    }
  }

  @Test
  public void test_decimalConvert() {
    BigDecimal bd = BigDecimal.valueOf(100500L);
    try (SettingsTransaction ignored = Turntables.setSettings(
        Settings.builder().decimalMode(Settings.DecimalMode.CONVERT).build())) {
      Tab tab = Turntables.tab()
          .row(bd)
          .row(null)
          .row(1)
          .row(2L)
          .row(3.0d)
          .row((IntSupplier) () -> 4)
          .row((LongSupplier) () -> 5L)
          .row((DoubleSupplier) () -> 6.0d);

      assertValuesAreInstancesOf(tab, BigDecimal.class);

      assertHasSingleColOfTyp(Typ.DECIMAL, Turntables.tab().row(bd).row(null));
    }
  }

  private static class Banana {
    public String toString() {
      return "banana";
    }
  }

  @Test
  public void test_unknown() {
    Object o = new Banana();

    Throwable t = catchThrowable(() -> Turntables.tab().row(o));
    assertThat(t)
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Unsupported object type: class " + getClass().getCanonicalName()
            + "$Banana");

    t = catchThrowable(() -> Turntables.tab().row(null).row(o));
    assertThat(t)
        .isExactlyInstanceOf(StructureException.class)
        .getCause()
        .isExactlyInstanceOf(IllegalArgumentException.class)
        .hasMessageStartingWith("Unsupported object type: class " + getClass().getCanonicalName()
            + "$Banana");
  }

  private void assertHasSingleColOfTyp(Typ typ, Tab tab) {
    assertThat(tab).isNotNull();
    assertThat(tab.cols()).hasSize(1);

    Tab.Col col = tab.cols().iterator().next();
    assertThat(col).isNotNull()
        .extracting(Tab.Col::typ)
        .isEqualTo(typ);
  }

  private void assertValuesAreInstancesOf(Tab tab, Class<?> klass) {
    for (Tab.Row row : tab.rows()) {
      for (Tab.Val val : row.vals()) {
        try {
          Object value = val.evaluate();
          if (value != null) {
            assertThat(value).isExactlyInstanceOf(klass);
          }
        } catch (AssertionEvaluationException aee) {
          // ignore
        }
      }
    }
  }
}
